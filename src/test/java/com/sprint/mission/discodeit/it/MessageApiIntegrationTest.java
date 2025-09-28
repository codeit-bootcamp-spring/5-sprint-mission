package com.sprint.mission.discodeit.it;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MessageApiIntegrationTest extends BaseIntegrationTest {

  @Autowired MockMvc mvc;

  @Test
  void create_find_list_delete() throws Exception {
    var uReq = new UserCreateRequest("neo","neo@matrix.io","pw");
    var ujson = om.writeValueAsString(uReq);
    var upart = new MockMultipartFile("userCreateRequest","user.json", MediaType.APPLICATION_JSON_VALUE, ujson.getBytes(StandardCharsets.UTF_8));
    var uRes = mvc.perform(multipart("/api/users").file(upart))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();
    var userId = UUID.fromString(om.readTree(uRes).get("id").asText());

    var chReq = new PublicChannelCreateRequest("general","desc");
    var chjson = om.writeValueAsString(chReq);
    var chRes = mvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(chjson))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();
    var channelId = UUID.fromString(om.readTree(chRes).get("id").asText());

    var mReq = new MessageCreateRequest("hello", channelId, userId);
    var mjson = om.writeValueAsString(mReq);
    var msgPart = new MockMultipartFile("messageCreateRequest","msg.json", MediaType.APPLICATION_JSON_VALUE, mjson.getBytes());

    var mRes = mvc.perform(multipart("/api/messages").file(msgPart))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.content").value("hello"))
        .andReturn().getResponse().getContentAsString();
    var messageId = UUID.fromString(om.readTree(mRes).get("id").asText());

    mvc.perform(get("/api/messages/{id}", messageId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(messageId.toString()));

    mvc.perform(get("/api/messages")
            .param("channelId", channelId.toString())
            .param("createdAt", Instant.now().toString())
            .param("page", "0")
            .param("size", "20"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].id").value(messageId.toString()));

    mvc.perform(delete("/api/messages/{id}", messageId))
        .andExpect(status().isNoContent());
  }

  @Test
  void find_notFound_returns404() throws Exception {
    mvc.perform(get("/api/messages/{id}", UUID.randomUUID()))
        .andExpect(status().isNotFound());
  }
}
