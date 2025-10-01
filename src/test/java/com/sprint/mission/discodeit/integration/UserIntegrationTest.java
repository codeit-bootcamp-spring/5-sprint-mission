package com.sprint.mission.discodeit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserStatusRepository userStatusRepository;

    @Test
    @DisplayName("사용자 생성 성공")
    void 사용자생성성공() throws Exception {
        // given
        var request = new UserCreateRequest("mike", "mike@test.com", "password123");

        MockMultipartFile jsonPart = new MockMultipartFile(
                "userCreateRequest",
                "user.json",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        // when & then
        mockMvc.perform(multipart("/api/users")
                        .file(jsonPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("mike"))
                .andExpect(jsonPath("$.email").value("mike@test.com"))
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    @DisplayName("사용자 수정 성공")
    void 사용자수정성공() throws Exception {
        // given
        User user = userRepository.save(new User("oldName", "old@test.com", "oldPassword", null));
        userStatusRepository.save(new UserStatus(user, Instant.now()));

        var updateRequest = new UserUpdateRequest("newName", "new@test.com", "newPassword123");
        MockMultipartFile jsonPart = new MockMultipartFile(
                "userUpdateRequest",
                "update.json",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(updateRequest)
        );

        // when & then
        mockMvc.perform(multipart("/api/users/{id}", user.getId())
                        .file(jsonPart)
                        .with(req -> { req.setMethod("PATCH"); return req; }) // multipart 기본이 POST라 PATCH로 강제 변경
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("newName"))
                .andExpect(jsonPath("$.email").value("new@test.com"));
    }

    @Test
    @DisplayName("사용자 삭제 성공")
    void 사용자삭제성공() throws Exception {
        // given
        User user = userRepository.save(new User("deleteMe", "delete@test.com", "deletePw", null));
        UUID userId = user.getId();

        // when & then
        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNoContent());

        assertThat(userRepository.existsById(userId)).isFalse();
    }

    @Test
    @DisplayName("모든 사용자 조회 성공")
    void 사용자목록조회성공() throws Exception {
        // given
        User user1 = userRepository.save(new User("mike", "mike@test.com", "pw123456", null));
        userStatusRepository.save(new UserStatus(user1, Instant.now()));

        User user2 = userRepository.save(new User("jane", "jane@test.com", "pw123456", null));
        userStatusRepository.save(new UserStatus(user2, Instant.now()));

        // when & then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("mike"))
                .andExpect(jsonPath("$[1].username").value("jane"));
    }
}