package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.config.StorageConfig;
import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.request.userStatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.user.UserDeleteResponse;
import com.sprint.mission.discodeit.dto.response.user.UserResponse;
import com.sprint.mission.discodeit.dto.response.userStatus.UserStatusResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.DuplicateUserException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@Import(StorageConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private UserStatusService userStatusService;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    private UUID userId;
    private Instant now;
    private User user;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        now = Instant.now();

        user = User.builder()
                .id(userId)
                .username("testuser")
                .defaultNickname("Test Nickname")
                .email("test@example.com")
                .createdAt(now)
                .updatedAt(now)
                .build();

        userResponse = UserResponse.builder()
                .id(userId)
                .username("testuser")
                .email("test@example.com")
                .nickname("Test Nickname")
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    @Test
    @DisplayName("?�체 ?�용??조회 ?�공")
    void getUserAll_success() throws Exception {
        // given
        UserResponse user1 = UserResponse.builder()
                .id(UUID.randomUUID())
                .username("user1")
                .email("user1@example.com")
                .nickname("User1")
                .createdAt(now)
                .build();

        UserResponse user2 = UserResponse.builder()
                .id(UUID.randomUUID())
                .username("user2")
                .email("user2@example.com")
                .nickname("User2")
                .createdAt(now)
                .build();

        given(userService.findAll()).willReturn(List.of(user1, user2));

        // when
        // then
        mockMvc.perform(get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("user1"))
                .andExpect(jsonPath("$[1].username").value("user2"));
    }

    @Test
    @DisplayName("?�용?�명?�로 ?�용??조회 ?�공")
    void getUserByUsername_success() throws Exception {
        // given
        given(userService.findByUsername("testuser")).willReturn(userResponse);
        given(userStatusService.isOnline(userId)).willReturn(true);

        // when
        // then
        mockMvc.perform(get("/api/users/username/testuser")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.online").value(true));
    }

    @Test
    @DisplayName("존재?��? ?�는 ?�용?�명?�로 조회 ??404 반환")
    void getUserByUsername_notFound() throws Exception {
        // given
        given(userService.findByUsername("nonexistent"))
                .willThrow(UserNotFoundException.withUsername("nonexistent"));

        // when
        // then
        mockMvc.perform(get("/api/users/username/nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("ID�??�용??조회 ?�공")
    void getUserById_success() throws Exception {
        // given
        given(userService.findById(userId)).willReturn(userResponse);
        given(userStatusService.isOnline(userId)).willReturn(false);

        // when
        // then
        mockMvc.perform(get("/api/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.online").value(false));
    }

    @Test
    @DisplayName("존재?��? ?�는 ID�?조회 ??404 반환")
    void getUserById_notFound() throws Exception {
        // given
        given(userService.findById(userId))
                .willThrow(UserNotFoundException.withId(userId));

        // when
        // then
        mockMvc.perform(get("/api/users/{userId}", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("?�용???�성 ?�공 - ?�로???��?지 ?�음")
    void createUser_success_withoutProfile() throws Exception {
        // given
        UserCreateRequest request = UserCreateRequest.builder()
                .username("newuser")
                .email("new@example.com")
                .password("password123")
                .defaultNickname("New User")
                .build();

        UserResponse response = UserResponse.builder()
                .id(UUID.randomUUID())
                .username("newuser")
                .email("new@example.com")
                .nickname("New User")
                .createdAt(now)
                .build();

        given(userService.create(any(UserCreateRequest.class))).willReturn(response);

        MockMultipartFile requestPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        // when
        // then
        mockMvc.perform(multipart("/api/users")
                        .file(requestPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.email").value("new@example.com"));
    }

    @Test
    @DisplayName("?�용???�성 ?�공 - ?�로???��?지 ?�함")
    void createUser_success_withProfile() throws Exception {
        // given
        UserCreateRequest request = UserCreateRequest.builder()
                .username("newuser")
                .email("new@example.com")
                .password("password123")
                .defaultNickname("New User")
                .build();

        UserResponse response = UserResponse.builder()
                .id(UUID.randomUUID())
                .username("newuser")
                .email("new@example.com")
                .nickname("New User")
                .createdAt(now)
                .build();

        given(userService.create(any(UserCreateRequest.class))).willReturn(response);

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
                "test image content".getBytes()
        );

        // when
        // then
        mockMvc.perform(multipart("/api/users")
                        .file(requestPart)
                        .file(profilePart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.username").value("newuser"));
    }

    @Test
    @DisplayName("중복 ?�용?�명?�로 ?�성 ??409 반환")
    void createUser_duplicateUsername() throws Exception {
        // given
        UserCreateRequest request = UserCreateRequest.builder()
                .username("existing")
                .email("new@example.com")
                .password("password123")
                .defaultNickname("New User")
                .build();

        given(userService.create(any(UserCreateRequest.class)))
                .willThrow(DuplicateUserException.withUsername("existing"));

        MockMultipartFile requestPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        // when
        // then
        mockMvc.perform(multipart("/api/users")
                        .file(requestPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("?�효?��? ?��? ?�청 ?�이?�로 ?�성 ??400 반환")
    void createUser_invalidData() throws Exception {
        // given
        UserCreateRequest invalidRequest = UserCreateRequest.builder()
                .username("")
                .email("invalid-email")
                .password("")
                .build();

        MockMultipartFile requestPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(invalidRequest)
        );

        // when
        // then
        mockMvc.perform(multipart("/api/users")
                        .file(requestPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("?�용???�보 ?�정 ?�공")
    void updateUser_success() throws Exception {
        // given
        UserUpdateRequest request = UserUpdateRequest.builder()
                .newUsername("UpdatedName")
                .build();

        UserResponse response = UserResponse.builder()
                .id(userId)
                .username("UpdatedName")
                .email("test@example.com")
                .nickname("UpdatedName")
                .createdAt(now)
                .updatedAt(now)
                .build();

        given(userService.update(eq(userId), any(UserUpdateRequest.class), any()))
                .willReturn(response);

        MockMultipartFile requestPart = new MockMultipartFile(
                "userUpdateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        // when
        // then
        mockMvc.perform(multipart("/api/users/{userId}", userId)
                        .file(requestPart)
                        .with(req -> {
                            req.setMethod("PATCH");
                            return req;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("UpdatedName"));
    }

    @Test
    @DisplayName("존재?��? ?�는 ?�용???�정 ??404 반환")
    void updateUser_notFound() throws Exception {
        // given
        UserUpdateRequest request = UserUpdateRequest.builder()
                .newUsername("UpdatedName")
                .build();

        given(userService.update(eq(userId), any(UserUpdateRequest.class), any()))
                .willThrow(UserNotFoundException.withId(userId));

        MockMultipartFile requestPart = new MockMultipartFile(
                "userUpdateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        // when
        // then
        mockMvc.perform(multipart("/api/users/{userId}", userId)
                        .file(requestPart)
                        .with(req -> {
                            req.setMethod("PATCH");
                            return req;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("존재?��? ?�는 ?�용????�� ??404 반환")
    void deleteUser_notFound() throws Exception {
        // given
        given(userService.delete(userId))
                .willThrow(UserNotFoundException.withId(userId));

        // when
        // then
        mockMvc.perform(delete("/api/users/{userId}", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("?�용????�� ?�공")
    void deleteUser_success() throws Exception {
        // given
        given(userService.delete(userId)).willReturn(UserDeleteResponse.success(user));

        // when
        // then
        mockMvc.perform(delete("/api/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.nickname").value("Test Nickname"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @DisplayName("?�용???�태 ?�데?�트 ?�공")
    void updateUserStatus_success() throws Exception {
        // given
        UserStatusUpdateRequest request = UserStatusUpdateRequest.builder()
                .newLastActiveAt(now)
                .build();

        UserStatusResponse response = UserStatusResponse.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .lastActiveAt(now)
                .createdAt(now)
                .updatedAt(now)
                .build();

        given(userStatusService.isOnline(userId)).willReturn(true);
        given(userStatusService.updateByUserId(userId, request)).willReturn(response);

        // when
        // then
        mockMvc.perform(patch("/api/users/{userId}/userStatus", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()));
    }

    @Test
    @DisplayName("리다?�렉???�이지 ?�청 ?�공")
    void userListPage_success() throws Exception {
        // when
        // then
        mockMvc.perform(get("/api/users/list"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/user-list.html"));
    }
}

