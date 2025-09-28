package com.sprint.mission.discodeit.it;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ReadStatusApiIntegrationTest extends BaseIntegrationTest {

  @Autowired MockMvc mvc;
  @Autowired UserRepository userRepository;
  @Autowired ChannelRepository channelRepository;

  @Test
  void create_and_listByUser() throws Exception {
    var user = userRepository.save(new User("neo","neo@matrix.io","pw", null));
    var channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "general", "desc"));

    var req = new ReadStatusCreateRequest(user.getId(), channel.getId(),
        Instant.parse("2025-01-01T00:00:00Z"));

    var created = mvc.perform(post("/api/readStatuses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsBytes(req)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.userId").value(user.getId().toString()))
        .andExpect(jsonPath("$.channelId").value(channel.getId().toString()))
        .andReturn().getResponse().getContentAsString();

    var readStatusId = UUID.fromString(om.readTree(created).get("id").asText());

    mvc.perform(get("/api/readStatuses/{id}", readStatusId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(readStatusId.toString()));

    mvc.perform(get("/api/readStatuses/user/{userId}", user.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].userId").value(user.getId().toString()))
        .andExpect(jsonPath("$[0].channelId").value(channel.getId().toString()));
  }

  @Test
  void create_conflict_whenAlreadyExists() throws Exception {
    var user = userRepository.save(new User("trinity","tri@matrix.io","pw", null));
    var channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "public", "d"));

    var body = om.writeValueAsBytes(new ReadStatusCreateRequest(
        user.getId(), channel.getId(), Instant.parse("2025-01-01T00:00:00Z")));

    mvc.perform(post("/api/readStatuses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
        .andExpect(status().isCreated());

    mvc.perform(get("/api/readStatuses")
            .param("userId", user.getId().toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].userId").value(user.getId().toString()))
        .andExpect(jsonPath("$[0].channelId").value(channel.getId().toString()));
  }
}
