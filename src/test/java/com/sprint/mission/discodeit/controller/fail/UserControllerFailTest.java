package com.sprint.mission.discodeit.controller.fail;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.domain.user.UserController;
import com.sprint.mission.discodeit.domain.user.UserService;
import com.sprint.mission.discodeit.domain.user.dto.UserCreateRequest;
import com.sprint.mission.discodeit.domain.user.exception.UserAlreadyExistsException;
import com.sprint.mission.discodeit.domain.userstatus.UserStatusService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerFailTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserStatusService userStatusService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입 시 중복된 이메일 사용")
    void create_duplicate_email() throws Exception {
        // Given
        UserCreateRequest requestDto = new UserCreateRequest("user", "user@gmail.com", "1234");
        String jsonRequestDto = objectMapper.writeValueAsString(requestDto);

        BDDMockito.given(userService.create(any(UserCreateRequest.class), any(Optional.class)))
                .willThrow(UserAlreadyExistsException.byEmail(requestDto.email()));

        MockMultipartFile jsonPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                jsonRequestDto.getBytes(StandardCharsets.UTF_8)
        );

        // When & Then
        mockMvc.perform(multipart("/api/users")
                        .file(jsonPart))
                .andExpect(status().isConflict()); // 409 Conflict 응답을 기대
    }

    @Test
    @DisplayName("사용자 삭제 실패")
    void delete_fail() throws Exception {
        // Given
        UUID nonExistentUserId = UUID.randomUUID();

        // UserService.delete가 NoSuchElementException을 던지도록 설정 (void 메소드)
        BDDMockito.willThrow(new NoSuchElementException("User with id " + nonExistentUserId + " not found"))
                .given(userService).delete(eq(nonExistentUserId));

        // When & Then
        mockMvc.perform(delete("/api/users/{userId}", nonExistentUserId))
                .andExpect(status().isNotFound()); // 404 Not Found 응답을 기대
    }
}
