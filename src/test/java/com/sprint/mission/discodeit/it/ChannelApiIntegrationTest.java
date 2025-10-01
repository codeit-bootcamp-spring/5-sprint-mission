package com.sprint.mission.discodeit.it;

import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ChannelApiIntegrationTest extends BaseIntegrationTest {

  @Autowired MockMvc mvc;

  @Test
  void createPublic_update_delete() throws Exception {
    var create = new PublicChannelCreateRequest("general","desc");
    var cjson = om.writeValueAsString(create);

    var created = mvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(cjson))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("general"))
        .andReturn().getResponse().getContentAsString();
    var id = UUID.fromString(om.readTree(created).get("id").asText());

    var upd = new PublicChannelUpdateRequest("newName","newDesc");
    mvc.perform(patch("/api/channels/{id}", id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(upd)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("newName"));

    mvc.perform(delete("/api/channels/{id}", id))
        .andExpect(status().isNoContent());
  }

  @Test
  void update_notFound_returns404() throws Exception {
    var upd = new PublicChannelUpdateRequest("n","d");
    mvc.perform(patch("/api/channels/{id}", UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(upd)))
        .andExpect(status().isNotFound());
  }
}
