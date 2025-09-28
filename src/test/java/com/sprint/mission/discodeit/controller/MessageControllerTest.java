package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.service.MessageService;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MessageController.class)
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class MessageControllerTest {

  @Autowired
  MockMvc mvc;
  @Autowired ObjectMapper om;

  @SuppressWarnings({"removal", "deprecation"})
  @org.springframework.boot.test.mock.mockito.MockBean
  MessageService messageService;

  @Test
  void create_success_multipart_withoutAttachments() throws Exception {
    var reqObj = new MessageCreateRequest("hello",
        UUID.fromString("d7423a12-a2c0-4ab2-a3eb-8ecb80908342"),
        UUID.fromString("a9445366-0adc-4835-94de-a7eb2d7e392c"));

    var json = om.writeValueAsString(reqObj);
    var messagePart = new MockMultipartFile(
        "messageCreateRequest", "message.json",
        MediaType.APPLICATION_JSON_VALUE, json.getBytes(StandardCharsets.UTF_8));

    var dto = new MessageDto(
        UUID.randomUUID(), Instant.now(), null, "hello",
        reqObj.channelId(),
        new UserDto(reqObj.authorId(), "neo", "neo@matrix.io", null, true),
        List.of()
    );

    given(messageService.create(eq(reqObj), ArgumentMatchers.<List<BinaryContentCreateRequest>>any()))
        .willReturn(dto);

    mvc.perform(multipart("/api/messages").file(messagePart))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.content").value("hello"))
        .andExpect(jsonPath("$.channelId").value(reqObj.channelId().toString()));
  }

  @Test
  void create_success_multipart_withAttachments() throws Exception {
    var reqObj = new MessageCreateRequest("hello",
        UUID.randomUUID(), UUID.randomUUID());
    var json = om.writeValueAsString(reqObj);

    var messagePart = new MockMultipartFile(
        "messageCreateRequest", "message.json",
        MediaType.APPLICATION_JSON_VALUE, json.getBytes(StandardCharsets.UTF_8));

    var f1 = new MockMultipartFile("attachments", "a.txt", "text/plain", "A".getBytes());
    var f2 = new MockMultipartFile("attachments", "b.png", "image/png", "B".getBytes());

    var dto = new MessageDto(UUID.randomUUID(), Instant.now(), null, "hello",
        reqObj.channelId(), null, List.of(
        new BinaryContentDto(UUID.randomUUID(), "a.txt", 1L, "text/plain"),
        new BinaryContentDto(UUID.randomUUID(), "b.png", 1L, "image/png")
    ));

    given(messageService.create(eq(reqObj), ArgumentMatchers.<List<BinaryContentCreateRequest>>any()))
        .willReturn(dto);

    mvc.perform(multipart("/api/messages")
            .file(messagePart)
            .file(f1)
            .file(f2))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.attachments[0].fileName").value("a.txt"))
        .andExpect(jsonPath("$.attachments[1].fileName").value("b.png"));
  }
}
