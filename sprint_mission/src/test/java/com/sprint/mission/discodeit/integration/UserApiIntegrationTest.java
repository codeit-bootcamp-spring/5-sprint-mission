package com.sprint.mission.discodeit.integration;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserApiIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserService userService;
    // 파일 IO 막기
    @MockitoBean
    private BinaryContentStorage fileStorage;

    @Test
    void createUser_success() throws Exception {
        // given
        var createReq = new UserCreateRequest(
                "user1",
                "user1@email.com",
                "password123"
        );

        MockMultipartFile userPart = new MockMultipartFile(
                "userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(createReq)
        );

        MockMultipartFile avatarPart = new MockMultipartFile(
                "avatar", "avatar.jpg", MediaType.IMAGE_JPEG_VALUE,
                "dummy-image".getBytes()
        );

        mockMvc.perform(multipart("/api/users")
                        .file(userPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("user1"))
                .andExpect(jsonPath("$.email").value("user1@email.com"));
    }
    @Test
    void findAllUsers_success() throws Exception {
        userService.create(new UserCreateRequest(
                "user1","user1@email.com","12341234"
        ), Optional.empty());
        userService.create(new UserCreateRequest(
                "user2","user2@email.com","12341234"
        ),Optional.empty());

        mockMvc.perform(get("/api/users")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].username",hasItems("user1", "user2")));

    }

    @Test
    @DisplayName("사용자 삭제 - 성공 후 메시지 확인")
    void deleteUser_success() throws Exception {
        // given: 한 명 생성
        UserDto created = userService.create(new UserCreateRequest(
                "user4", "user4@email.com", "12341234"), Optional.empty());

        // when & then
        mockMvc.perform(delete("/api/users/{userId}", created.id()))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateUser_success() throws Exception {
// 1. DB에 테스트용 사용자 생성
        UserDto createdUser = userService.create(
                new UserCreateRequest("user1", "user1@email.com", "12341234"),
                java.util.Optional.empty()
        );

        UUID userId = createdUser.id();

        // 2. 업데이트 요청 객체
        UserUpdateRequest updateReq = new UserUpdateRequest(
                "updatedUser",
                "updated@email.com",
                "12341234"
        );

        // 3. JSON part
        MockMultipartFile jsonPart = new MockMultipartFile(
                "userUpdateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(updateReq)
        );

        // 4. Optional 파일 part
        MockMultipartFile profilePart = new MockMultipartFile(
                "profile",
                "profile.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "dummy-image".getBytes()
        );

        // 5. 실제 요청 수행 (PATCH + multipart)
        mockMvc.perform(multipart("/api/users/{userId}", userId)
                        .file(jsonPart)
                        .file(profilePart)
                        .with(request -> { request.setMethod("PATCH"); return request; })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value("updatedUser"))
                .andExpect(jsonPath("$.email").value("updated@email.com"));
    }




}
