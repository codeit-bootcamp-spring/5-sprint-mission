package com.sprint.mission.discodeit.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.config.TestSecurityConfig;
import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.UserDto;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
class ChannelIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  private UUID createTestUser(String username, String email) throws Exception {
    UserDto.CreateRequest createRequest = UserDto.CreateRequest.builder()
                                                               .username(username)
                                                               .email(email)
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

    return objectMapper.readValue(response, UserDto.DetailResponse.class)
                       .getId();
  }

  @Test
  @Transactional
  @WithMockUser(username = "manager", roles = "CHANNEL_MANAGER")
  void testCreateAndGetPublicChannel() throws Exception {
    UUID userId = createTestUser("channelUser1", "channel1@test.com");

    ChannelDto.CreateRequest createRequest = ChannelDto.CreateRequest.builder()
                                                                     .name("Public Channel")
                                                                     .description(
                                                                         "Integration public channel")
                                                                     .build();

    String response = mockMvc.perform(
                                 post("/api/channels/public").contentType(MediaType.APPLICATION_JSON)
                                                             .content(objectMapper.writeValueAsString(createRequest)))
                             .andExpect(status().isCreated())
                             .andExpect(jsonPath("$.name").value("Public Channel"))
                             .andReturn()
                             .getResponse()
                             .getContentAsString();

    UUID channelId = objectMapper.readValue(response, ChannelDto.DetailResponse.class)
                                 .getId();

    mockMvc.perform(put("/api/channels/" + channelId).contentType(MediaType.APPLICATION_JSON)
                                                     .content(objectMapper.writeValueAsString(
                                                         ChannelDto.UpdateRequest.builder()
                                                                                 .name(
                                                                                     "Updated Channel")
                                                                                 .build())))
           .andExpect(status().isOk());
    ;

    mockMvc.perform(get("/api/channels").param("userId", userId.toString())
                                        .contentType(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$[?(@.id=='" + channelId + "')]").exists());
  }

  @Test
  @Transactional
  @WithMockUser(username = "manager", roles = "CHANNEL_MANAGER")
  void testUpdateAndDeleteChannel() throws Exception {
    UUID userId = createTestUser("channelUser2", "channel2@test.com");

    ChannelDto.CreateRequest createRequest = ChannelDto.CreateRequest.builder()
                                                                     .name("Private Channel")
                                                                     .description(
                                                                         "Integration private channel")
                                                                     .participantIds(
                                                                         List.of(userId))
                                                                     .build();

    String response = mockMvc.perform(
                                 post("/api/channels/private").contentType(MediaType.APPLICATION_JSON)
                                                              .content(objectMapper.writeValueAsString(createRequest)))
                             .andExpect(status().isCreated())
                             .andReturn()
                             .getResponse()
                             .getContentAsString();

    UUID channelId = objectMapper.readValue(response, ChannelDto.DetailResponse.class)
                                 .getId();

    mockMvc.perform(delete("/api/channels/" + channelId))
           .andExpect(status().isNoContent());

    mockMvc.perform(get("/api/channels").param("userId", userId.toString())
                                        .contentType(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$[?(@.id=='" + channelId + "')]").doesNotExist());
  }
}
