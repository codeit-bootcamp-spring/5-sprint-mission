package com.sprint.mission.discodeit.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
public class ChannelApiIntegrationTest {

  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private MockMvc mockMvc;

  // ===== Helpers =====
  private String requestBody(Object body) throws Exception {
    return objectMapper.writeValueAsString(body);
  }

  // ========= Create =========
  @Nested
  class CreatePublicChannel {

    @Test
    @DisplayName("POST /api/channels/public - 성공 시 201")
    void success_201() throws Exception {
      var req = new PublicChannelCreateRequest("public", "publicDesc");

      mockMvc.perform(
              post("/api/channels/public")
                  .content(requestBody(req))
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isCreated())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.name").value("public"))
          .andExpect(jsonPath("$.description").value("publicDesc"))
          .andExpect(jsonPath("$.type").value(ChannelType.PUBLIC.name()));
    }

    @Test
    @DisplayName("POST /api/channels/public - 요청 유효성 검증 실패 시 400")
    void bad_request_400() throws Exception {
      var req = new PublicChannelCreateRequest("", "p".repeat(501));

      mockMvc.perform(
              post("/api/channels/public")
                  .content(requestBody(req))
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  class CreatePrivateChannel {

    @Test
    @DisplayName("POST /api/channels/private - 성공 시 201")
    @Sql("/seed.sql")
    void success_201() throws Exception {
      UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
      PrivateChannelCreateRequest req = new PrivateChannelCreateRequest(List.of(userId));

      mockMvc.perform(
              post("/api/channels/private")
                  .content(requestBody(req))
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isCreated())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.name").isEmpty())
          .andExpect(jsonPath("$.description").isEmpty())
          .andExpect(jsonPath("$.type").value(ChannelType.PRIVATE.name()))
          .andExpect(jsonPath("$.participants").isNotEmpty());
    }

    @Test
    @DisplayName("POST /api/channels/private - 유저가 존재하지 않을 시 404")
    void not_found_404() throws Exception {
      PrivateChannelCreateRequest req = new PrivateChannelCreateRequest(List.of(UUID.randomUUID()));

      mockMvc.perform(
              post("/api/channels/private")
                  .content(requestBody(req))
                  .contentType(MediaType.APPLICATION_JSON)
                  .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/channels/private - 요청 유효성 검증 실패 시 400")
    void bad_request_400() throws Exception {
      PrivateChannelCreateRequest req = new PrivateChannelCreateRequest(List.of());

      mockMvc.perform(
              post("/api/channels/private")
                  .content(requestBody(req))
                  .contentType(MediaType.APPLICATION_JSON)
                  .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isBadRequest());
    }
  }

  // ========= Update =========
  @Nested
  class updateChannel {

    @Test
    @DisplayName("PATCH /api/channels/{channelId} - 성공 시 200")
    @Sql("/seed.sql")
    void success_200() throws Exception {
      UUID channelId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaa0001");
      PublicChannelUpdateRequest req =
          new PublicChannelUpdateRequest("updated", "updatedDesc");

      mockMvc.perform(
              patch("/api/channels/{channelId}", channelId)
                  .content(requestBody(req))
                  .contentType(MediaType.APPLICATION_JSON)
                  .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.name").value("updated"))
          .andExpect(jsonPath("$.description").value("updatedDesc"))
          .andExpect(jsonPath("$.type").value(ChannelType.PUBLIC.name()));
    }

    @Test
    @DisplayName("PATCH /api/channels/{channelId} - 존재하지 않을 시 404")
    void not_found_404() throws Exception {
      UUID channelId = UUID.randomUUID();
      PublicChannelUpdateRequest req =
          new PublicChannelUpdateRequest("updated", "updatedDesc");

      mockMvc.perform(
              patch("/api/channels/{channelId}", channelId)
                  .content(requestBody(req))
                  .contentType(MediaType.APPLICATION_JSON)
                  .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH /api/channels/{channelId} - private 채널 업데이트 시도 시 400")
    @Sql("/seed.sql")
    void bad_request_400() throws Exception {
      UUID channelId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaa0002");
      PublicChannelUpdateRequest req =
          new PublicChannelUpdateRequest("updated", "updatedDesc");

      mockMvc.perform(
              patch("/api/channels/{channelId}", channelId)
                  .content(requestBody(req))
                  .contentType(MediaType.APPLICATION_JSON)
                  .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isBadRequest());
    }
  }

  // ========= Delete =========
  @Nested
  class deleteChannel {

    @Test
    @DisplayName("DELETE /api/channels/{channelId} - 성공 시 204")
    @Sql("/seed.sql")
    void success_204() throws Exception {
      UUID channelId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaa0001");

      mockMvc.perform(
              delete("/api/channels/{channelId}", channelId))
          .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/channels/{channelId} - 존재하지 않을 시 404")
    void not_found_404() throws Exception {
      UUID channelId = UUID.randomUUID();

      mockMvc.perform(
              delete("/api/channels/{channelId}", channelId)
                  .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound());
    }
  }
}
