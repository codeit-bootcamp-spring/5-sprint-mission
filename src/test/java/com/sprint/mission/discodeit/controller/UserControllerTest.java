package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.config.TestSecurityConfig;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.user.DuplicateEmailException;
import com.sprint.mission.discodeit.exception.user.DuplicateUsernameException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = UserController.class, excludeFilters = @ComponentScan.Filter(
    type = FilterType.REGEX, pattern = ".*\\.security\\..*|.*\\.config\\.SecurityConfig"))
@Import({GlobalExceptionHandler.class, TestSecurityConfig.class})
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("POST /api/users - 성공: 사용자 생성 (프로필 없음)")
    void create_WithoutProfile_Success() throws Exception {
        // given
        UserCreateRequest request = new UserCreateRequest(
            "testuser",
            "test@example.com",
            "password123"
        );
        UserDto response = new UserDto(
            UUID.randomUUID(),
            "testuser",
            "test@example.com",
            null,
            true,
            Role.USER
        );

        MockMultipartFile requestPart = new MockMultipartFile(
            "userCreateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request)
        );

        given(userService.create(any(UserCreateRequest.class), eq(null)))
            .willReturn(response);

        // when & then
        mockMvc.perform(multipart("/api/users")
                .file(requestPart)
                .with(csrf()))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.username").value("testuser"))
            .andExpect(jsonPath("$.email").value("test@example.com"));

        then(userService).should().create(any(UserCreateRequest.class), eq(null));
    }

    @Test
    @DisplayName("POST /api/users - 성공: 사용자 생성 (프로필 포함)")
    void create_WithProfile_Success() throws Exception {
        // given
        UserCreateRequest request = new UserCreateRequest(
            "testuser",
            "test@example.com",
            "password123"
        );
        UUID profileId = UUID.randomUUID();
        UserDto response = new UserDto(
            UUID.randomUUID(),
            "testuser",
            "test@example.com",
            new com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto(
                profileId,
                "profile.jpg",
                1024L,
                "image/jpeg"
            ),
            true,
            Role.USER
        );

        MockMultipartFile requestPart = new MockMultipartFile(
            "userCreateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request)
        );
        MockMultipartFile profilePart = new MockMultipartFile(
            "profile",
            "profile.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            "test image".getBytes()
        );

        given(userService.create(any(UserCreateRequest.class), any()))
            .willReturn(response);

        // when & then
        mockMvc.perform(multipart("/api/users")
                .file(requestPart)
                .file(profilePart)
                .with(csrf()))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.username").value("testuser"))
            .andExpect(jsonPath("$.profile.id").value(profileId.toString()));

        then(userService).should().create(any(UserCreateRequest.class), any());
    }

    @Test
    @DisplayName("POST /api/users - 실패: 잘못된 요청 데이터 (유효성 검증 실패)")
    void create_InvalidData_BadRequest() throws Exception {
        // given - username이 빈 문자열
        UserCreateRequest request = new UserCreateRequest(
            "",
            "test@example.com",
            "password123"
        );

        MockMultipartFile requestPart = new MockMultipartFile(
            "userCreateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request)
        );

        // when & then
        mockMvc.perform(multipart("/api/users")
                .file(requestPart)
                .with(csrf()))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/users - 실패: 중복된 사용자명")
    void create_DuplicateUsername_Conflict() throws Exception {
        // given
        UserCreateRequest request = new UserCreateRequest(
            "existinguser",
            "new@example.com",
            "password123"
        );

        MockMultipartFile requestPart = new MockMultipartFile(
            "userCreateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request)
        );

        given(userService.create(any(UserCreateRequest.class), eq(null)))
            .willThrow(new DuplicateUsernameException("existinguser"));

        // when & then
        mockMvc.perform(multipart("/api/users")
                .file(requestPart)
                .with(csrf()))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("DUPLICATE_USERNAME"))
            .andExpect(jsonPath("$.message").value("중복된 사용자명입니다."))
            .andExpect(jsonPath("$.details.username").value("existinguser"));

        then(userService).should().create(any(UserCreateRequest.class), eq(null));
    }

    @Test
    @DisplayName("POST /api/users - 실패: 중복된 이메일")
    void create_DuplicateEmail_Conflict() throws Exception {
        // given
        UserCreateRequest request = new UserCreateRequest(
            "newuser",
            "existing@example.com",
            "password123"
        );

        MockMultipartFile requestPart = new MockMultipartFile(
            "userCreateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request)
        );

        given(userService.create(any(UserCreateRequest.class), eq(null)))
            .willThrow(new DuplicateEmailException("existing@example.com"));

        // when & then
        mockMvc.perform(multipart("/api/users")
                .file(requestPart)
                .with(csrf()))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("DUPLICATE_EMAIL"))
            .andExpect(jsonPath("$.message").value("중복된 이메일입니다."))
            .andExpect(jsonPath("$.details.email").value("existing@example.com"));

        then(userService).should().create(any(UserCreateRequest.class), eq(null));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/users - 성공: 사용자 목록 조회")
    void findAll_Success() throws Exception {
        // given
        List<UserDto> users = List.of(
            new UserDto(UUID.randomUUID(), "user1", "user1@example.com", null, true, Role.USER),
            new UserDto(UUID.randomUUID(), "user2", "user2@example.com", null, false, Role.USER)
        );

        given(userService.findAll()).willReturn(users);

        // when & then
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].username").value("user1"))
            .andExpect(jsonPath("$[1].username").value("user2"));

        then(userService).should().findAll();
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/users - 성공: 빈 사용자 목록")
    void findAll_EmptyList() throws Exception {
        // given
        given(userService.findAll()).willReturn(List.of());

        // when & then
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));

        then(userService).should().findAll();
    }

    @Test
    @WithMockUser
    @DisplayName("PATCH /api/users/{userId} - 성공: 사용자 정보 수정")
    void update_Success() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UserUpdateRequest request = new UserUpdateRequest(null, "updated@example.com", null);
        UserDto response = new UserDto(
            userId,
            "testuser",
            "updated@example.com",
            null,
            true,
            Role.USER
        );

        MockMultipartFile requestPart = new MockMultipartFile(
            "userUpdateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request)
        );

        given(userService.update(eq(userId), any(UserUpdateRequest.class), eq(null)))
            .willReturn(response);

        // when & then
        mockMvc.perform(multipart("/api/users/{userId}", userId)
                .file(requestPart)
                .with(csrf())
                .with(req -> {
                    req.setMethod("PATCH");
                    return req;
                }))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("updated@example.com"));

        then(userService).should().update(eq(userId), any(UserUpdateRequest.class), eq(null));
    }

    @Test
    @WithMockUser
    @DisplayName("PATCH /api/users/{userId} - 실패: 존재하지 않는 사용자")
    void update_UserNotFound() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UserUpdateRequest request = new UserUpdateRequest(null, "updated@example.com", null);

        MockMultipartFile requestPart = new MockMultipartFile(
            "userUpdateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(request)
        );

        given(userService.update(eq(userId), any(UserUpdateRequest.class), eq(null)))
            .willThrow(new UserNotFoundException());

        // when & then
        mockMvc.perform(multipart("/api/users/{userId}", userId)
                .file(requestPart)
                .with(csrf())
                .with(req -> {
                    req.setMethod("PATCH");
                    return req;
                }))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));

        then(userService).should().update(eq(userId), any(UserUpdateRequest.class), eq(null));
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /api/users/{userId} - 성공: 사용자 삭제")
    void delete_Success() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        willDoNothing().given(userService).delete(userId);

        // when & then
        mockMvc.perform(delete("/api/users/{userId}", userId)
                .with(csrf()))
            .andExpect(status().isNoContent());

        then(userService).should().delete(userId);
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /api/users/{userId} - 실패: 존재하지 않는 사용자")
    void delete_UserNotFound() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        willThrow(new UserNotFoundException()).given(userService).delete(userId);

        // when & then
        mockMvc.perform(delete("/api/users/{userId}", userId)
                .with(csrf()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));

        then(userService).should().delete(userId);
    }
}
