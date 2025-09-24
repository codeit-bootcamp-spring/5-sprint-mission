package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.domain.binarycontent.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.domain.user.UserController;
import com.sprint.mission.discodeit.domain.user.UserService;
import com.sprint.mission.discodeit.domain.user.dto.UserCreateRequest;
import com.sprint.mission.discodeit.domain.user.dto.UserDto;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc; // HTTP 요청을 보낼 MockMvc 객체

    @MockitoBean
    private UserService userService; // 가짜 서비스 객체(Mock)

    @MockitoBean
    private UserStatusService userStatusService;


    // ObjectMapper는 JSON 직렬화/역직렬화를 위해 필요할 수 있습니다.
    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @DisplayName("회원가입 성공 테스트")
    void create_success() throws Exception {
        // Given
        UserCreateRequest requestDto = new UserCreateRequest("user", "user@gmail.com", "1234");
        String jsonRequestDto = objectMapper.writeValueAsString(requestDto);

        UserDto userDto = UserDto.builder().id(UUID.randomUUID()).email("user@gmail.com").username("user").build();

        Optional<BinaryContentCreateRequest> profileRequest = Optional.empty();

        BDDMockito.given(userService.create(any(UserCreateRequest.class), eq(Optional.empty()))).willReturn(userDto);

        MockMultipartFile jsonPart = new MockMultipartFile(
                "userCreateRequest", // Controller에서 @RequestPart 로 받을 이름
                "",
                MediaType.APPLICATION_JSON_VALUE,
                jsonRequestDto.getBytes(StandardCharsets.UTF_8)
        );

        // When
        mockMvc.perform(
                        multipart("/api/users")
                                .file(jsonPart))
                // Then
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("user"))
                .andExpect(jsonPath("$.email").value("user@gmail.com"));

    }
}
