package com.sprint.mission.discodeit.controller.success;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.domain.user.UserController;
import com.sprint.mission.discodeit.domain.user.UserService;
import com.sprint.mission.discodeit.domain.user.dto.UserCreateRequest;
import com.sprint.mission.discodeit.domain.user.dto.UserDto;
import com.sprint.mission.discodeit.domain.user.dto.UserUpdateRequest;
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
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
public class UserControllerSuccessTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserStatusService userStatusService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @DisplayName("회원가입 성공 테스트")
    void create_success() throws Exception {
        // Given
        UserCreateRequest requestDto = new UserCreateRequest("user", "user@gmail.com", "1234");
        String jsonRequestDto = objectMapper.writeValueAsString(requestDto);
        UserDto response = UserDto.builder().id(UUID.randomUUID()).email("user@gmail.com").username("user").build();

        BDDMockito.given(userService.create(any(UserCreateRequest.class), any(Optional.class)))
                .willReturn(response);

        MockMultipartFile jsonPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                jsonRequestDto.getBytes(StandardCharsets.UTF_8)
        );

        // When
        mockMvc.perform( multipart("/api/users")
                .file(jsonPart))
                // Then
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("user"))
                .andExpect(jsonPath("$.email").value("user@gmail.com"));

    }

    @Test
    @DisplayName("사용자 정보 수정 테스트")
    void update_success() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();
        UserUpdateRequest requestDto = new UserUpdateRequest("updatedUser", "updated@gmail.com", "5678");
        String jsonRequestDto = objectMapper.writeValueAsString(requestDto);
        UserDto responseDto = UserDto.builder().id(userId).username("updatedUser").email("updated@gmail.com").build();

        // UserService의 update 메소드가 호출될 때의 동작을 정의
        // any(UUID.class) 등으로 어떤 값이 들어와도 괜찮다고 설정
        BDDMockito.given(userService.update(eq(userId), any(UserUpdateRequest.class), any(Optional.class))).willReturn(responseDto);

        MockMultipartFile jsonPart = new MockMultipartFile(
                "userUpdateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                jsonRequestDto.getBytes(StandardCharsets.UTF_8)
        );

        // When
        mockMvc.perform(multipart("/api/users/{userId}", userId)
                        .file(jsonPart)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        }))
                // Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value("updatedUser"))
                .andExpect(jsonPath("$.email").value("updated@gmail.com"));
    }

    @Test
    @DisplayName("사용자 삭제 테스트")
    void delete_success() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();
        BDDMockito.willDoNothing().given(userService).delete(userId);

        // When
        mockMvc.perform(delete("/api/users/{userId}", userId))
                // Then
                .andExpect(status().isNoContent());

        BDDMockito.then(userService).should(times(1)).delete(userId);
    }
}
