package com.sprint.mission.discodeit.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
public class MessageApiIntegrationTest {

  @Autowired private ObjectMapper objectMapper;
  @Autowired private MockMvc mockMvc;

  // ===== Helpers =====
  private MockMultipartFile jsonPart(String name, Object body) throws Exception {
    return new MockMultipartFile(
        name, "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(body));
  }

  private MockMultipartFile jpg(String name) {
    return new MockMultipartFile(name, "p.jpg", MediaType.IMAGE_JPEG_VALUE, "img".getBytes());
  }

  private String requestBody(Object body) throws Exception {
    return objectMapper.writeValueAsString(body);
  }

  // ========= Create =========
  @Nested
  class CreateMessage {

    @Test
    @DisplayName("POST /api/messages - 성공(201)")
    @Sql("/seed.sql")
    void success() throws Exception {
      UUID channelId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaa0001");
      UUID authorId  = UUID.fromString("11111111-1111-1111-1111-111111111111");

      // 메인 시그니처: (channelId, authorId, content)
      MessageCreateRequest req = new MessageCreateRequest(channelId, authorId, "hello");

      mockMvc.perform(
              multipart("/api/messages")
                  .file(jsonPart("messageCreateRequest", req))
                  .file(jpg("attachments"))
                  .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isCreated())
          .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.id").exists())
          .andExpect(jsonPath("$.createdAt").exists())
          .andExpect(jsonPath("$.content").value("hello"))
          .andExpect(jsonPath("$.author.id").exists())
          .andExpect(jsonPath("$.channelId").exists())
          .andExpect(jsonPath("$.attachments").isNotEmpty());
    }

    @Test
    @DisplayName("POST /api/messages - 요청 값 검증 실패(400)")
    void bad_request() throws Exception {
      MessageCreateRequest req = new MessageCreateRequest(null, null, "hello");

      mockMvc.perform(
              multipart("/api/messages")
                  .file(jsonPart("messageCreateRequest", req))
                  .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/messages - 유저 또는 채널 존재하지 않음(404)")
    void not_found() throws Exception {
      UUID channelId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaa0001");
      UUID authorId  = UUID.fromString("11111111-1111-1111-1111-111111111111");
      MessageCreateRequest req = new MessageCreateRequest(channelId, authorId, "hello");

      mockMvc.perform(
              multipart("/api/messages")
                  .file(jsonPart("messageCreateRequest", req))
                  .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound());
    }
  }

  // ========= Update =========
  @Nested
  class UpdateMessage {

    @Test
    @DisplayName("PATCH /api/messages/{messageId} - 성공(200)")
    @Sql("/seed.sql")
    void success() throws Exception {
      UUID messageId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbb0001");
      MessageUpdateRequest req = new MessageUpdateRequest("updated");

      mockMvc.perform(
              patch("/api/messages/{messageId}", messageId)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(requestBody(req))
                  .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.id").exists())
          .andExpect(jsonPath("$.createdAt").exists())
          .andExpect(jsonPath("$.content").value("updated"))
          .andExpect(jsonPath("$.author.id").exists())
          .andExpect(jsonPath("$.channelId").exists());
    }

    @Test
    @DisplayName("PATCH /api/messages/{messageId} - 존재하지 않음(404)")
    void not_found() throws Exception {
      UUID messageId = UUID.randomUUID();
      MessageUpdateRequest req = new MessageUpdateRequest("updated");

      mockMvc.perform(
              patch("/api/messages/{messageId}", messageId)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(requestBody(req))
                  .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound())
          .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.code").value(ErrorCode.MESSAGE_NOT_FOUND.name()))
          .andExpect(jsonPath("$.message").value(ErrorCode.MESSAGE_NOT_FOUND.getMessage()))
          .andExpect(jsonPath("$.status").value(ErrorCode.MESSAGE_NOT_FOUND.getStatus().value()))
          .andExpect(jsonPath("$.exceptionType").value("MessageNotFoundException"));
    }
  }

  // ========= Delete =========
  @Nested
  class DeleteMessage {

    @Test
    @DisplayName("DELETE /api/messages/{messageId} - 성공(204)")
    @Sql("/seed.sql")
    void success() throws Exception {
      UUID messageId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbb0001");

      mockMvc.perform(delete("/api/messages/{messageId}", messageId))
          .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/messages/{messageId} - 존재하지 않음(404)")
    void not_found() throws Exception {
      UUID messageId = UUID.randomUUID();

      mockMvc.perform(
              delete("/api/messages/{messageId}", messageId)
                  .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound())
          .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
  }

  // ========= FindAll =========
  @Nested
  class FindAllMessages {

    @Test
    @DisplayName("GET /api/messages - 성공(200)")
    @Sql("/seed.sql")
    void success() throws Exception {
      UUID channelId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaa0001");

      mockMvc.perform(
              get("/api/messages")
                  .param("channelId", channelId.toString())
                  .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.content", hasSize(5)))
          .andExpect(jsonPath("$.content[0].channelId").value(channelId.toString()))
          .andExpect(jsonPath("$.hasNext").value(false));
    }

    @Test
    @DisplayName("GET /api/messages - 채널이 존재하지 않음(404)")
    void not_found() throws Exception {
      UUID channelId = UUID.randomUUID();

      mockMvc.perform(
              get("/api/messages")
                  .param("channelId", channelId.toString())
                  .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound())
          .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
  }
}
