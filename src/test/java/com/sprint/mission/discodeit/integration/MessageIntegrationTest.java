package com.sprint.mission.discodeit.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.MessageDto;
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
class MessageIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  private UUID createUser(String username, String email) throws Exception {
    UserDto.CreateRequest req = UserDto.CreateRequest.builder()
                                                     .username(username)
                                                     .email(email)
                                                     .password("password")
                                                     .build();

    MockMultipartFile userPart = new MockMultipartFile("userCreateRequest", "", "application/json",
        objectMapper.writeValueAsBytes(req));
    MockMultipartFile profilePart = new MockMultipartFile("profile", "profile.png", "image/png",
        "dummy".getBytes());

    String resp = mockMvc.perform(multipart("/api/users").file(userPart)
                                                         .file(profilePart)
                                                         .contentType(
                                                             MediaType.MULTIPART_FORM_DATA))
                         .andExpect(status().isCreated())
                         .andReturn()
                         .getResponse()
                         .getContentAsString();
    return UUID.fromString(objectMapper.readTree(resp)
                                       .get("id")
                                       .asText());
  }

  private UUID createChannel(String name) throws Exception {
    ChannelDto.CreateRequest req = ChannelDto.CreateRequest.builder()
                                                           .name(name)
                                                           .build();
    String resp = mockMvc.perform(
                             multipart("/api/channels/public").contentType(MediaType.APPLICATION_JSON)
                                                              .content(objectMapper.writeValueAsBytes(req)))
                         .andExpect(status().isCreated())
                         .andReturn()
                         .getResponse()
                         .getContentAsString();
    return UUID.fromString(objectMapper.readTree(resp)
                                       .get("id")
                                       .asText());
  }

  private UUID createMessage(UUID channelId, UUID userId, String content) throws Exception {
    MessageDto.CreateRequest req = MessageDto.CreateRequest.builder()
                                                           .channelId(channelId)
                                                           .authorId(userId)
                                                           .content(content)
                                                           .build();

    MockMultipartFile msgPart = new MockMultipartFile("messageCreateRequest", "",
        "application/json", objectMapper.writeValueAsBytes(req));
    MockMultipartFile attachment = new MockMultipartFile("attachments", "attach.txt", "text/plain",
        "dummy attachment".getBytes());

    String resp = mockMvc.perform(multipart("/api/messages").file(msgPart)
                                                            .file(attachment)
                                                            .contentType(
                                                                MediaType.MULTIPART_FORM_DATA))
                         .andExpect(status().isCreated())
                         .andReturn()
                         .getResponse()
                         .getContentAsString();
    return UUID.fromString(objectMapper.readTree(resp)
                                       .get("id")
                                       .asText());
  }


  @Test
  @Transactional
  void testCreateAndGetMessage() throws Exception {
    UUID userId = createUser("msgUser", "msgUser@test.com");
    UUID channelId = createChannel("msgChannel");
    UUID messageId = createMessage(channelId, userId, "Hello World");

    mockMvc.perform(get("/api/messages").param("channelId", channelId.toString())
                                        .contentType(MediaType.APPLICATION_JSON))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.content[0].id").value(messageId.toString()));
  }

  @Test
  @Transactional
  void testUpdateMessage() throws Exception {
    UUID userId = createUser("updateUser", "updateUser@test.com");
    UUID channelId = createChannel("updateChannel");
    UUID messageId = createMessage(channelId, userId, "Original Content");

    MessageDto.UpdateRequest updateReq = MessageDto.UpdateRequest.builder()
                                                                 .newContent("Updated Content")
                                                                 .build();

    mockMvc.perform(patch("/api/messages/" + messageId).contentType(MediaType.APPLICATION_JSON)
                                                       .content(objectMapper.writeValueAsBytes(
                                                           updateReq)))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.content").value("Updated Content"));
  }

  @Test
  @Transactional
  void testDeleteMessage() throws Exception {
    UUID userId = createUser("deleteUser", "deleteUser@test.com");
    UUID channelId = createChannel("deleteChannel");
    UUID messageId = createMessage(channelId, userId, "To be deleted");

    mockMvc.perform(delete("/api/messages/" + messageId))
           .andExpect(status().isNoContent());
  }
}
