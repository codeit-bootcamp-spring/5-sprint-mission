package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.service.ChannelService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChannelController.class)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class ChannelControllerTest {

  @Autowired
  MockMvc mvc;
  @Autowired ObjectMapper om;

  @SuppressWarnings({"removal", "deprecation"})
  @org.springframework.boot.test.mock.mockito.MockBean
  ChannelService channelService;

  @Test
  void createPublic_success() throws Exception {
    var req = new PublicChannelCreateRequest("general","desc");
    var dto = new ChannelDto(UUID.randomUUID(), ChannelType.PUBLIC, "general","desc", List.of(), null);

    given(channelService.create(eq(req))).willReturn(dto);

    mvc.perform(post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(req)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.type").value("PUBLIC"))
        .andExpect(jsonPath("$.name").value("general"));
  }

  @Test
  void createPrivate_success() throws Exception {
    var u1 = UUID.randomUUID(); var u2 = UUID.randomUUID();
    var req = new PrivateChannelCreateRequest(List.of(u1, u2));
    var dto = new ChannelDto(
        UUID.randomUUID(), ChannelType.PRIVATE, null, null,
        List.of(new UserDto(u1,"u1","u1@x.io", null, true),
            new UserDto(u2,"u2","u2@x.io", null, false)),
        null);

    given(channelService.create(eq(req))).willReturn(dto);

    mvc.perform(post("/api/channels/private")
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(req)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.type").value("PRIVATE"))
        .andExpect(jsonPath("$.participants").isArray());
  }

  @Test
  void updatePublic_notFound() throws Exception {
    var id = UUID.randomUUID();
    var req = new PublicChannelUpdateRequest("newName","newDesc");

    willThrow(new ChannelNotFoundException(id)).given(channelService).update(eq(id), eq(req));

    mvc.perform(patch("/api/channels/{channelId}", id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(new PublicChannelUpdateRequest("newName","newDesc"))))
        .andExpect(status().isNotFound());
  }
}
