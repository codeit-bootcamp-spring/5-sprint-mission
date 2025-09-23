package com.sprint.mission.discodeit.controller;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.mapper.MultipartFileMapper;
import com.sprint.mission.discodeit.service.MessageService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = MessageController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import(GlobalExceptionHandler.class)
class MessageControllerTest {

  @Autowired
  MockMvc mockMvc;
  @Autowired
  ObjectMapper objectMapper;

  @MockitoBean
  private JpaMetamodelMappingContext mappingContext;
  @MockitoBean
  MessageService messageService;
  @MockitoBean
  MultipartFileMapper multipartFileMapper;

  // ====== POST /api/messages (multipart) ======

  @Test
  @DisplayName("create - 성공 시 201과 MessageDto 반환")
  void create_success_createsMessage201() throws Exception {
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    MessageCreateRequest req = new MessageCreateRequest("hello", authorId, channelId);

    MockMultipartFile jsonPart = new MockMultipartFile(
        "messageCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(req)
    );
    MockMultipartFile file1 = new MockMultipartFile(
        "attachments", "a.txt", MediaType.TEXT_PLAIN_VALUE, "A".getBytes()
    );

    MessageDto dto = MessageDto.builder()
        .id(UUID.randomUUID())
        .createdAt(Instant.parse("2025-01-01T00:00:00Z"))
        .updatedAt(Instant.parse("2025-01-01T00:00:00Z"))
        .content("hello")
        .channelId(channelId)
        .author(UserDto.builder()
            .id(authorId)
            .username("u1")
            .email("u1@test.com")
            .profile(null)
            .online(false)
            .build())
        .attachments(List.of())
        .build();

    given(messageService.create(any())).willReturn(dto);

    mockMvc.perform(
            multipart("/api/messages")
                .file(jsonPart)
                .file(file1)
                .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isCreated())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$.content").value("hello"))
        .andExpect(jsonPath("$.channelId").value(channelId.toString()));
  }

  @Test
  @DisplayName("create - DTO 유효성 검증 실패 시 400(BAD_REQUEST)")
  void create_validationError_returns400() throws Exception {
    MessageCreateRequest invalid = new MessageCreateRequest(null, null, null);
    MockMultipartFile jsonPart = new MockMultipartFile(
        "messageCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
        objectMapper.writeValueAsBytes(invalid)
    );

    mockMvc.perform(
            multipart("/api/messages")
                .file(jsonPart)
                .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

    then(messageService).shouldHaveNoInteractions();
  }

  // ====== PATCH /api/messages/{messageId} ======

  @Test
  @DisplayName("update - 성공 시 200과 갱신된 MessageDto 반환")
  void update_success_updatesMessage200() throws Exception {
    UUID messageId = UUID.randomUUID();
    MessageUpdateRequest req = new MessageUpdateRequest("updated");
    MessageDto dto = MessageDto.builder()
        .id(messageId)
        .createdAt(Instant.parse("2025-01-01T00:00:00Z"))
        .updatedAt(Instant.parse("2025-01-02T00:00:00Z"))
        .content("updated")
        .channelId(UUID.randomUUID())
        .author(null)
        .attachments(List.of())
        .build();

    given(messageService.update(any(), any())).willReturn(dto);

    mockMvc.perform(
            patch("/api/messages/{messageId}", messageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
                .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(messageId.toString()))
        .andExpect(jsonPath("$.content").value("updated"));
  }

  @Test
  @DisplayName("update - 메시지가 없으면 404(NOT_FOUND)")
  void update_messageNotFound_returns404() throws Exception {
    UUID messageId = UUID.randomUUID();
    MessageUpdateRequest req = new MessageUpdateRequest("updated");

    DiscodeitException ex = MessageNotFoundException.withDetail("id", messageId);
    given(messageService.update(any(), any())).willThrow(ex);

    mockMvc.perform(
            patch("/api/messages/{messageId}", messageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
                .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isNotFound())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.code").value(ex.getErrorCode().name()))
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.message").value(ex.getErrorCode().getMessage()))
        .andExpect(jsonPath("$.details.id").value(messageId.toString()));
  }

  // ====== DELETE /api/messages/{messageId} ======

  @Test
  @DisplayName("delete - 성공 시 204(NO_CONTENT)")
  void delete_success_returns204() throws Exception {
    UUID messageId = UUID.randomUUID();
    willDoNothing().given(messageService).delete(messageId);

    mockMvc.perform(delete("/api/messages/{messageId}", messageId))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("delete - 메시지가 없으면 404(NOT_FOUND)")
  void delete_messageNotFound_returns404() throws Exception {
    UUID messageId = UUID.randomUUID();
    DiscodeitException ex = MessageNotFoundException.withDetail("id", messageId);
    willThrow(ex).given(messageService).delete(messageId);

    mockMvc.perform(
            delete("/api/messages/{messageId}", messageId).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.code").value(ex.getErrorCode().name()))
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.details.id").value(messageId.toString()));
  }

  // ====== GET /api/messages?channelId=...&cursor=... ======

  @Test
  @DisplayName("findAllByChannelId - 성공 시 200과 PageResponse<MessageDto> 반환")
  void findAllByChannelId_success_returns200Page() throws Exception {
    UUID channelId = UUID.randomUUID();

    PageResponse<MessageDto> page = new PageResponse<>(
        List.of(
            MessageDto.builder()
                .id(UUID.randomUUID())
                .createdAt(Instant.parse("2025-01-01T00:00:00Z"))
                .updatedAt(Instant.parse("2025-01-01T00:00:00Z"))
                .content("m1")
                .channelId(channelId)
                .author(null)
                .attachments(List.of())
                .build()
        ),
        null,
        1,
        false,
        null
    );
    given(messageService.findAllByChannelId(any(), any(), any())).willReturn(page);

    mockMvc.perform(
            get("/api/messages")
                .param("channelId", channelId.toString())
                .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content[0].channelId").value(channelId.toString()))
        .andExpect(jsonPath("$.hasNext").value(false));
  }

  @Test
  @DisplayName("findAllByChannelId - channelId 누락/형식오류 시 500(INTERNAL_SERVER_ERROR)")
  void findAllByChannelId_missingOrInvalidChannelId_returns500() throws Exception {
    mockMvc.perform(get("/api/messages").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

    mockMvc.perform(get("/api/messages")
            .param("channelId", "not-a-uuid")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

    then(messageService).shouldHaveNoInteractions();
  }
}