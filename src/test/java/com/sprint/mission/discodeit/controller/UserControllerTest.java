package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    UserService userService;

    @MockitoBean
    UserStatusService userStatusService;

    @Test
    @DisplayName("유저 생성 성공")
    void createUserSuccess() throws Exception {
        UserDto responseDto = new UserDto(
                UUID.randomUUID(),
                "mike",
                "mike@test.com",
                null,
                true
        );

        Mockito.when(userService.create(any(UserCreateRequest.class), any()))
                .thenReturn(responseDto);

        UserCreateRequest request = new UserCreateRequest("mike", "mike@test.com", "12345678");

        mockMvc.perform(multipart("/api/users")
                        .file(new MockMultipartFile(
                                "userCreateRequest",
                                "",
                                MediaType.APPLICATION_JSON_VALUE,
                                objectMapper.writeValueAsBytes(request)
                        ))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("mike"))
                .andExpect(jsonPath("$.email").value("mike@test.com"));
    }

    @Test
    @DisplayName("유저 전체 조회 성공")
    void findAllUsersSuccess() throws Exception {
        List<UserDto> users = List.of(
                new UserDto(UUID.randomUUID(), "alice", "alice@test.com", null, true),
                new UserDto(UUID.randomUUID(), "bob", "bob@test.com", null, false)
        );

        Mockito.when(userService.findAll()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("alice"))
                .andExpect(jsonPath("$[1].username").value("bob"));
    }

    @Test
    @DisplayName("유저 삭제 성공")
    void deleteUserSuccess() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(delete("/api/users/{userId}", userId))
                .andExpect(status().isNoContent());

        Mockito.verify(userService).delete(eq(userId));
    }

    @Test
    @DisplayName("유저 수정 실패 - 잘못된 입력값")
    void updateUserFailInvalidRequest() throws Exception {
        UUID userId = UUID.randomUUID();
        UserUpdateRequest request = new UserUpdateRequest("a", "invalid-email", "short");

        mockMvc.perform(multipart("/api/users/{userId}", userId)
                        .file(new MockMultipartFile(
                                "userUpdateRequest",
                                "",
                                MediaType.APPLICATION_JSON_VALUE,
                                objectMapper.writeValueAsBytes(request)
                        ))
                        .with(req -> { req.setMethod("PATCH"); return req; })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isBadRequest());
    }
}