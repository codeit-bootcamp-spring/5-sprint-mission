package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.service.ReadStatusService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReadStatusController.class)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class ReadStatusControllerTest {

  @Autowired
  MockMvc mvc;
  @Autowired ObjectMapper om;

  @SuppressWarnings({"removal", "deprecation"})
  @org.springframework.boot.test.mock.mockito.MockBean
  ReadStatusService readStatusService;

  @Test
  void create_success() throws Exception {
    var userId = UUID.randomUUID();
    var channelId = UUID.randomUUID();
    var req = new ReadStatusCreateRequest(userId, channelId, Instant.parse("2025-01-01T00:00:00Z"));
    var dto = new ReadStatusDto(UUID.randomUUID(), userId, channelId, req.lastReadAt());

    given(readStatusService.create(eq(req))).willReturn(dto);

    mvc.perform(post("/api/readStatuses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(req)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.userId").value(userId.toString()))
        .andExpect(jsonPath("$.channelId").value(channelId.toString()));
  }

  @Test
  void update_success() throws Exception {
    var id = UUID.randomUUID();
    var req = new ReadStatusUpdateRequest(Instant.parse("2025-01-02T00:00:00Z"));
    var dto = new ReadStatusDto(id, UUID.randomUUID(), UUID.randomUUID(), req.newLastReadAt());

    given(readStatusService.update(eq(id), eq(req))).willReturn(dto);

    mvc.perform(patch("/api/readStatuses/{id}", id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(om.writeValueAsString(req)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.lastReadAt").value("2025-01-02T00:00:00Z"));
  }

  @Test
  void listByUser_success() throws Exception {
    var userId = UUID.randomUUID();
    var dto = new ReadStatusDto(UUID.randomUUID(), userId, UUID.randomUUID(), Instant.now());

    given(readStatusService.findAllByUserId(eq(userId))).willReturn(List.of(dto));

    mvc.perform(get("/api/readStatuses")
            .param("userId", userId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].userId").value(userId.toString()));
  }
}
