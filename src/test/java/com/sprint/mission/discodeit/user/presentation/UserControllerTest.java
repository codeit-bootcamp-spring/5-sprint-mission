package com.sprint.mission.discodeit.user.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.global.error.GlobalExceptionHandler;
import com.sprint.mission.discodeit.global.security.jwt.JwtAuthenticationFilter;
import com.sprint.mission.discodeit.global.security.jwt.JwtLogoutHandler;
import com.sprint.mission.discodeit.global.security.ratelimit.LoginRateLimitFilter;
import com.sprint.mission.discodeit.global.security.userdetails.DiscodeitUserDetails;
import com.sprint.mission.discodeit.global.security.userdetails.dto.UserDetailsDto;
import com.sprint.mission.discodeit.support.TestSecurityConfig;
import com.sprint.mission.discodeit.user.application.UserService;
import com.sprint.mission.discodeit.user.domain.Role;
import com.sprint.mission.discodeit.user.domain.exception.DuplicateEmailException;
import com.sprint.mission.discodeit.user.domain.exception.DuplicateUsernameException;
import com.sprint.mission.discodeit.user.domain.exception.UserNotFoundException;
import com.sprint.mission.discodeit.user.presentation.dto.UserCreateRequest;
import com.sprint.mission.discodeit.user.presentation.dto.UserDto;
import com.sprint.mission.discodeit.user.presentation.dto.UserUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    controllers = UserController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {
            JwtAuthenticationFilter.class,
            LoginRateLimitFilter.class,
            JwtLogoutHandler.class
        }
    )
)
@Import({GlobalExceptionHandler.class, TestSecurityConfig.class})
@DisplayName("UserController 테스트")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    private static final UUID TEST_USER_ID = UUID.randomUUID();

    private void setAuthenticatedUser() {
        UserDetailsDto userDetailsDto = new UserDetailsDto(UserControllerTest.TEST_USER_ID, "testuser", Role.USER);
        DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userDetailsDto, "password");
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private UserDto createUserDto(UUID userId, String username, String email) {
        return new UserDto(userId, username, email, null, true, Role.USER);
    }

    @Nested
    @DisplayName("POST /api/users")
    class Create {

        @Test
        @WithMockUser
        @DisplayName("유효한 요청 시 201 반환")
        void create_withValidRequest_returns201() throws Exception {
            // given
            UserCreateRequest request = new UserCreateRequest("newuser", "newuser@example.com", "password123");
            UserDto responseDto = createUserDto(UUID.randomUUID(), "newuser", "newuser@example.com");

            MockMultipartFile requestPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
            );

            given(userService.create(any(UserCreateRequest.class), isNull()))
                .willReturn(responseDto);

            // when & then
            mockMvc.perform(multipart("/api/users")
                    .file(requestPart))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.email").value("newuser@example.com"));
        }

        @Test
        @WithMockUser
        @DisplayName("프로필 이미지 포함 요청 시 201 반환")
        void create_withProfile_returns201() throws Exception {
            // given
            UserCreateRequest request = new UserCreateRequest("newuser", "newuser@example.com", "password123");
            UserDto responseDto = createUserDto(UUID.randomUUID(), "newuser", "newuser@example.com");

            MockMultipartFile requestPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
            );

            MockMultipartFile profilePart = new MockMultipartFile(
                "profile",
                "profile.png",
                MediaType.IMAGE_PNG_VALUE,
                "fake-image-content".getBytes()
            );

            given(userService.create(any(UserCreateRequest.class), any()))
                .willReturn(responseDto);

            // when & then
            mockMvc.perform(multipart("/api/users")
                    .file(requestPart)
                    .file(profilePart))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("newuser"));
        }

        @Test
        @WithMockUser
        @DisplayName("username 비어있음 시 400 반환")
        void create_withBlankUsername_returns400() throws Exception {
            // given
            UserCreateRequest request = new UserCreateRequest("", "newuser@example.com", "password123");

            MockMultipartFile requestPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
            );

            // when & then
            mockMvc.perform(multipart("/api/users")
                    .file(requestPart))
                .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser
        @DisplayName("email 비어있음 시 400 반환")
        void create_withBlankEmail_returns400() throws Exception {
            // given
            UserCreateRequest request = new UserCreateRequest("newuser", "", "password123");

            MockMultipartFile requestPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
            );

            // when & then
            mockMvc.perform(multipart("/api/users")
                    .file(requestPart))
                .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser
        @DisplayName("password 비어있음 시 400 반환")
        void create_withBlankPassword_returns400() throws Exception {
            // given
            UserCreateRequest request = new UserCreateRequest("newuser", "newuser@example.com", "");

            MockMultipartFile requestPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
            );

            // when & then
            mockMvc.perform(multipart("/api/users")
                    .file(requestPart))
                .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser
        @DisplayName("중복된 username 시 409 반환")
        void create_withDuplicateUsername_returns409() throws Exception {
            // given
            UserCreateRequest request = new UserCreateRequest("existinguser", "newuser@example.com", "password123");

            MockMultipartFile requestPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
            );

            given(userService.create(any(UserCreateRequest.class), isNull()))
                .willThrow(new DuplicateUsernameException("existinguser"));

            // when & then
            mockMvc.perform(multipart("/api/users")
                    .file(requestPart))
                .andExpect(status().isConflict());
        }

        @Test
        @WithMockUser
        @DisplayName("중복된 email 시 409 반환")
        void create_withDuplicateEmail_returns409() throws Exception {
            // given
            UserCreateRequest request = new UserCreateRequest("newuser", "existing@example.com", "password123");

            MockMultipartFile requestPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
            );

            given(userService.create(any(UserCreateRequest.class), isNull()))
                .willThrow(new DuplicateEmailException("existing@example.com"));

            // when & then
            mockMvc.perform(multipart("/api/users")
                    .file(requestPart))
                .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("GET /api/users")
    class FindAll {

        @Test
        @DisplayName("인증된 사용자 조회 시 200 반환")
        void findAll_withAuthenticatedUser_returns200() throws Exception {
            // given
            setAuthenticatedUser();
            UserDto user1 = createUserDto(UUID.randomUUID(), "user1", "user1@example.com");
            UserDto user2 = createUserDto(UUID.randomUUID(), "user2", "user2@example.com");

            given(userService.findAll()).willReturn(List.of(user1, user2));

            // when & then
            mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[1].username").value("user2"));
        }

        @Test
        @DisplayName("사용자 없음 시 빈 배열 반환")
        void findAll_withNoUsers_returnsEmptyArray() throws Exception {
            // given
            setAuthenticatedUser();

            given(userService.findAll()).willReturn(List.of());

            // when & then
            mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("인증 없음 시 403 반환")
        void findAll_withoutAuth_returns403() throws Exception {
            // when & then
            mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("PATCH /api/users/{userId}")
    class Update {

        @Test
        @DisplayName("유효한 요청 시 200 반환")
        void update_withValidRequest_returns200() throws Exception {
            // given
            setAuthenticatedUser();
            UserUpdateRequest request = new UserUpdateRequest("updateduser", "updated@example.com", null);
            UserDto responseDto = createUserDto(TEST_USER_ID, "updateduser", "updated@example.com");

            MockMultipartFile requestPart = new MockMultipartFile(
                "userUpdateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
            );

            given(userService.update(
                eq(TEST_USER_ID), any(UserUpdateRequest.class), isNull(), any(), any()))
                .willReturn(responseDto);

            // when & then
            mockMvc.perform(multipart("/api/users/{userId}", TEST_USER_ID)
                    .file(requestPart)
                    .with(req -> {
                        req.setMethod("PATCH");
                        return req;
                    }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updateduser"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));
        }

        @Test
        @DisplayName("프로필 이미지 포함 요청 시 200 반환")
        void update_withProfile_returns200() throws Exception {
            // given
            setAuthenticatedUser();
            UserUpdateRequest request = new UserUpdateRequest("updateduser", null, null);
            UserDto responseDto = createUserDto(TEST_USER_ID, "updateduser", "test@example.com");

            MockMultipartFile requestPart = new MockMultipartFile(
                "userUpdateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
            );

            MockMultipartFile profilePart = new MockMultipartFile(
                "profile",
                "new-profile.png",
                MediaType.IMAGE_PNG_VALUE,
                "new-fake-image-content".getBytes()
            );

            given(userService.update(eq(TEST_USER_ID), any(UserUpdateRequest.class), any(), any(), any()))
                .willReturn(responseDto);

            // when & then
            mockMvc.perform(multipart("/api/users/{userId}", TEST_USER_ID)
                    .file(requestPart)
                    .file(profilePart)
                    .with(req -> {
                        req.setMethod("PATCH");
                        return req;
                    }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updateduser"));
        }

        @Test
        @DisplayName("인증 없음 시 403 반환")
        void update_withoutAuth_returns403() throws Exception {
            // given
            UserUpdateRequest request = new UserUpdateRequest("updateduser", null, null);

            MockMultipartFile requestPart = new MockMultipartFile(
                "userUpdateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
            );

            // when & then
            mockMvc.perform(multipart("/api/users/{userId}", TEST_USER_ID)
                    .file(requestPart)
                    .with(req -> {
                        req.setMethod("PATCH");
                        return req;
                    }))
                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("존재하지 않는 사용자 시 404 반환")
        void update_withNonExistingUser_returns404() throws Exception {
            // given
            setAuthenticatedUser();
            UUID nonExistingId = UUID.randomUUID();
            UserUpdateRequest request = new UserUpdateRequest("updateduser", null, null);

            MockMultipartFile requestPart = new MockMultipartFile(
                "userUpdateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
            );

            given(userService.update(
                eq(nonExistingId), any(UserUpdateRequest.class), isNull(), any(), any()))
                .willThrow(new UserNotFoundException(nonExistingId));

            // when & then
            mockMvc.perform(multipart("/api/users/{userId}", nonExistingId)
                    .file(requestPart)
                    .with(req -> {
                        req.setMethod("PATCH");
                        return req;
                    }))
                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("중복된 username 시 409 반환")
        void update_withDuplicateUsername_returns409() throws Exception {
            // given
            setAuthenticatedUser();
            UserUpdateRequest request = new UserUpdateRequest("existinguser", null, null);

            MockMultipartFile requestPart = new MockMultipartFile(
                "userUpdateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
            );

            given(userService.update(
                eq(TEST_USER_ID), any(UserUpdateRequest.class), isNull(), any(), any()))
                .willThrow(new DuplicateUsernameException("existinguser"));

            // when & then
            mockMvc.perform(multipart("/api/users/{userId}", TEST_USER_ID)
                    .file(requestPart)
                    .with(req -> {
                        req.setMethod("PATCH");
                        return req;
                    }))
                .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("중복된 email 시 409 반환")
        void update_withDuplicateEmail_returns409() throws Exception {
            // given
            setAuthenticatedUser();
            UserUpdateRequest request = new UserUpdateRequest(null, "existing@example.com", null);

            MockMultipartFile requestPart = new MockMultipartFile(
                "userUpdateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
            );

            given(userService.update(
                eq(TEST_USER_ID), any(UserUpdateRequest.class), isNull(), any(), any()))
                .willThrow(new DuplicateEmailException("existing@example.com"));

            // when & then
            mockMvc.perform(multipart("/api/users/{userId}", TEST_USER_ID)
                    .file(requestPart)
                    .with(req -> {
                        req.setMethod("PATCH");
                        return req;
                    }))
                .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("DELETE /api/users/{userId}")
    class DeleteById {

        @Test
        @DisplayName("유효한 요청 시 204 반환")
        void deleteById_withValidRequest_returns204() throws Exception {
            // given
            setAuthenticatedUser();

            willDoNothing().given(userService).deleteById(TEST_USER_ID);

            // when & then
            mockMvc.perform(delete("/api/users/{userId}", TEST_USER_ID))
                .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("인증 없음 시 403 반환")
        void deleteById_withoutAuth_returns403() throws Exception {
            // when & then
            mockMvc.perform(delete("/api/users/{userId}", TEST_USER_ID))
                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("존재하지 않는 사용자 시 404 반환")
        void deleteById_withNonExistingUser_returns404() throws Exception {
            // given
            setAuthenticatedUser();
            UUID nonExistingId = UUID.randomUUID();

            willThrow(new UserNotFoundException(nonExistingId))
                .given(userService).deleteById(nonExistingId);

            // when & then
            mockMvc.perform(delete("/api/users/{userId}", nonExistingId))
                .andExpect(status().isNotFound());
        }
    }
}
