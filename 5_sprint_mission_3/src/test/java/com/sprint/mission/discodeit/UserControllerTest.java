package com.sprint.mission.discodeit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createUserTest() throws Exception {
        UserCreateRequest request = new UserCreateRequest("testuser", "test@example.com", "1234");

        MockMultipartFile userPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                "application/json",
                objectMapper.writeValueAsBytes(request)
        );

        MockMultipartFile profilePart = new MockMultipartFile(
                "profile",
                "profile.png",
                "image/png",
                "dummy image data".getBytes()
        );

        mockMvc.perform(multipart("/api/user/create")
                        .file(userPart)
                        .file(profilePart))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testuser"));
    }
}