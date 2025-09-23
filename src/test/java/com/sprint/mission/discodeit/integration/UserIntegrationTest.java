package com.sprint.mission.discodeit.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.UserDto;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @Transactional
  void testCreateAndGetUser() throws Exception {

    UserDto.CreateRequest createRequest = UserDto.CreateRequest.builder()
                                                               .username("integrationUser")
                                                               .email("integration@test.com")
                                                               .password("password")
                                                               .build();

    MockMultipartFile userPart = new MockMultipartFile("userCreateRequest", "", "application/json",
        objectMapper.writeValueAsBytes(createRequest));

    MockMultipartFile profilePart = new MockMultipartFile("profile", "profile.png", "image/png",
        "dummy".getBytes());

    String response = mockMvc.perform(multipart("/api/users").file(userPart)
                                                             .file(profilePart)
                                                             .contentType(
                                                                 MediaType.MULTIPART_FORM_DATA))
                             .andExpect(status().isCreated())
                             .andExpect(jsonPath("$.username").value("integrationUser"))
                             .andReturn()
                             .getResponse()
                             .getContentAsString();

    UserDto.DetailResponse createdUser = objectMapper.readValue(response,
        UserDto.DetailResponse.class);
    UUID userId = createdUser.getId();

    mockMvc.perform(get("/api/users").contentType(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$[?(@.id=='" + userId + "')]").exists());
  }

  @Test
  @Transactional
  void testUpdateUser() throws Exception {
    // 1. 사용자 생성
    UserDto.CreateRequest createRequest = UserDto.CreateRequest.builder()
                                                               .username("updateUser")
                                                               .email("update@test.com")
                                                               .password("password")
                                                               .build();

    MockMultipartFile userPart = new MockMultipartFile("userCreateRequest", "", "application/json",
        objectMapper.writeValueAsBytes(createRequest));

    MockMultipartFile profilePart = new MockMultipartFile("profile", "profile.png", "image/png",
        "dummy".getBytes());

    String response = mockMvc.perform(multipart("/api/users").file(userPart)
                                                             .file(profilePart)
                                                             .contentType(
                                                                 MediaType.MULTIPART_FORM_DATA))
                             .andExpect(status().isCreated())
                             .andReturn()
                             .getResponse()
                             .getContentAsString();

    UserDto.DetailResponse createdUser = objectMapper.readValue(response,
        UserDto.DetailResponse.class);
    UUID userId = createdUser.getId();

    UserDto.UpdateRequest updateRequest = UserDto.UpdateRequest.builder()
                                                               .newUsername("updatedUser")
                                                               .newEmail("updated@test.com")
                                                               .build();

    MockMultipartFile updatePart = new MockMultipartFile("userUpdateRequest", "",
        "application/json", objectMapper.writeValueAsBytes(updateRequest));

    mockMvc.perform(multipart("/api/users/" + userId).file(updatePart)
                                                     .with(request -> {
                                                       request.setMethod("PATCH");
                                                       return request;
                                                     })
                                                     .contentType(MediaType.MULTIPART_FORM_DATA))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.username").value("updatedUser"))
           .andExpect(jsonPath("$.email").value("updated@test.com"));
  }

  @Test
  @Transactional
  void testDeleteUser() throws Exception {

    UserDto.CreateRequest createRequest = UserDto.CreateRequest.builder()
                                                               .username("deleteUser")
                                                               .email("delete@test.com")
                                                               .password("password")
                                                               .build();

    MockMultipartFile userPart = new MockMultipartFile("userCreateRequest", "", "application/json",
        objectMapper.writeValueAsBytes(createRequest));

    MockMultipartFile profilePart = new MockMultipartFile("profile", "profile.png", "image/png",
        "dummy".getBytes());

    String response = mockMvc.perform(multipart("/api/users").file(userPart)
                                                             .file(profilePart)
                                                             .contentType(
                                                                 MediaType.MULTIPART_FORM_DATA))
                             .andExpect(status().isCreated())
                             .andReturn()
                             .getResponse()
                             .getContentAsString();

    UserDto.DetailResponse createdUser = objectMapper.readValue(response,
        UserDto.DetailResponse.class);
    UUID userId = createdUser.getId();

    mockMvc.perform(delete("/api/users/" + userId))
           .andExpect(status().isNoContent());

    mockMvc.perform(get("/api/users").contentType(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$[?(@.id=='" + userId + "')]").doesNotExist());
  }
}
