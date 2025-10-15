package com.sprint.mission.discodeit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.user.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class UserApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private UserResponse createUser(String username, String email, String nickname) throws Exception {
        UserCreateRequest request = UserCreateRequest.builder()
                .username(username)
                .email(email)
                .password("password123")
                .defaultNickname(nickname)
                .build();

        MockMultipartFile requestPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        String response = mockMvc.perform(multipart("/api/users")
                        .file(requestPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        return objectMapper.readValue(response, UserResponse.class);
    }

    @Test
    @DisplayName("1.1 사용자 생성 후 조회 성공")
    void createUserAndGet_success() throws Exception {
        // when
        UserResponse created = createUser("testuser", "test@example.com", "Test User");

        // then
        mockMvc.perform(get("/api/users/{userId}", created.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        // then
        mockMvc.perform(get("/api/users/username/{username}", "testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("1.2 중복 사용자 생성 실패")
    void createDuplicateUser_failure() throws Exception {
        // given
        userRepository.save(new User("duplicate", "password", "Existing", "existing@example.com", null));

        UserCreateRequest request = UserCreateRequest.builder()
                .username("duplicate")
                .email("new@example.com")
                .password("password123")
                .defaultNickname("New User")
                .build();

        MockMultipartFile requestPart = new MockMultipartFile(
                "userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        // when
        // then
        mockMvc.perform(multipart("/api/users")
                        .file(requestPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(ErrorCode.DUPLICATE_LOGIN_ID.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.DUPLICATE_LOGIN_ID.getMessage()));

        assertThat(userRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("1.3 존재하지 않는 사용자 조회 실패")
    void getUser_failure() throws Exception {
        // given
        UserResponse userResponse = createUser("validuser", "valid@example.com", "Valid User");

        // when
        // then
        mockMvc.perform(get("/api/users/{userId}", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_FOUND.getMessage()));

        assertThat(userRepository.findById(userResponse.getId())).isPresent();
    }

    @Test
    @DisplayName("2.1 사용자 수정 성공")
    void updateUser_success() throws Exception {
        // given
        UserResponse created = createUser("originaluser", "original@example.com", "Original");
        UUID userId = created.getId();

        UserUpdateRequest updateRequest = UserUpdateRequest.builder()
                .newUsername("updateduser")
                .newEmail("updated@example.com")
                .newPassword("newpass123")
                .build();

        MockMultipartFile updatePart = new MockMultipartFile(
                "userUpdateRequest", "", MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(updateRequest)
        );

        // when
        mockMvc.perform(multipart("/api/users/{userId}", userId)
                        .file(updatePart)
                        .with(req -> { req.setMethod("PATCH"); return req; })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updateduser"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));

        // then
        assertThat(userRepository.findById(userId))
                .get()
                .extracting(User::getUsername, User::getEmail)
                .containsExactly("updateduser", "updated@example.com");
    }

    @Test
    @DisplayName("2.2 사용자 수정 실패 - 중복")
    void updateUser_failure() throws Exception {
        // given
        User u1 = userRepository.save(new User("user1", "pass", "User1", "user1@example.com", null));
        userRepository.save(new User("user2", "pass", "User2", "user2@example.com", null));

        UserUpdateRequest request = UserUpdateRequest.builder().newUsername("user2").build();
        MockMultipartFile file = new MockMultipartFile("userUpdateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(request));

        // when
        // then
        mockMvc.perform(multipart("/api/users/{userId}", u1.getId())
                        .file(file)
                        .with(req -> { req.setMethod("PATCH"); return req; })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(ErrorCode.DUPLICATE_LOGIN_ID.name()));
    }

    @Test
    @DisplayName("3.1 사용자 삭제 성공")
    void deleteUser_success() throws Exception {
        // given
        UserResponse created = createUser("tobedeleted", "delete@example.com", "ToDelete");
        UUID userId = created.getId();

        // when
        mockMvc.perform(delete("/api/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value("tobedeleted"));

        // then
        assertThat(userRepository.findById(userId)).isEmpty();
        assertThat(userRepository.findById(userId)).isEmpty();
    }

    @Test
    @DisplayName("3.2 존재하지 않는 사용자 삭제 실패")
    void deleteUser_failure() throws Exception {
        // when
        // then
        mockMvc.perform(delete("/api/users/{userId}", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.name()))
                .andExpect(jsonPath("$.message").value(ErrorCode.USER_NOT_FOUND.getMessage()));
    }
}