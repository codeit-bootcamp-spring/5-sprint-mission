package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MessageController.class)
class MessageControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    MessageService messageService;

    @Test
    @DisplayName("메시지 생성 성공")
    void createMessageSuccess() throws Exception {
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        MessageDto responseDto = new MessageDto(
                UUID.randomUUID(),
                Instant.now(),
                Instant.now(),
                "Hello world!",
                channelId,
                new UserDto(authorId, "alice", "alice@test.com", null, true),
                List.of()
        );

        Mockito.when(messageService.create(any(MessageCreateRequest.class), any()))
                .thenReturn(responseDto);

        MessageCreateRequest request = new MessageCreateRequest(
                "Hello world!",
                channelId,
                authorId
        );

        mockMvc.perform(multipart("/api/messages")
                        .file(new MockMultipartFile(
                                "messageCreateRequest",
                                "",
                                MediaType.APPLICATION_JSON_VALUE,
                                objectMapper.writeValueAsBytes(request)
                        ))
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Hello world!"));
    }

    @Test
    @DisplayName("메시지 수정 성공")
    void updateMessageSuccess() throws Exception {
        UUID messageId = UUID.randomUUID();

        MessageDto updatedDto = new MessageDto(
                messageId,
                Instant.now(),
                Instant.now(),
                "Updated content",
                UUID.randomUUID(),
                null,
                List.of()
        );

        Mockito.when(messageService.update(eq(messageId), any(MessageUpdateRequest.class)))
                .thenReturn(updatedDto);

        MessageUpdateRequest request = new MessageUpdateRequest("Updated content");

        mockMvc.perform(patch("/api/messages/{messageId}", messageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated content"));
    }

    @Test
    @DisplayName("메시지 삭제 성공")
    void deleteMessageSuccess() throws Exception {
        UUID messageId = UUID.randomUUID();

        mockMvc.perform(delete("/api/messages/{messageId}", messageId))
                .andExpect(status().isNoContent());

        Mockito.verify(messageService).delete(eq(messageId));
    }

    @Test
    @DisplayName("메시지 목록 조회 성공")
    void findAllMessagesSuccess() throws Exception {
        UUID channelId = UUID.randomUUID();

        List<MessageDto> messages = List.of(
                new MessageDto(UUID.randomUUID(), Instant.now(), Instant.now(),
                        "msg1", channelId, null, List.of()),
                new MessageDto(UUID.randomUUID(), Instant.now(), Instant.now(),
                        "msg2", channelId, null, List.of())
        );

        PageResponse<MessageDto> pageResponse = new PageResponse<>(messages, null, 2, false, 2L);

        Mockito.when(messageService.findAllByChannelId(eq(channelId), any(), any()))
                .thenReturn(pageResponse);

        mockMvc.perform(get("/api/messages")
                        .param("channelId", channelId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].content").value("msg1"))
                .andExpect(jsonPath("$.content[1].content").value("msg2"));
    }

    @Test
    @DisplayName("메시지 수정 실패 - 잘못된 요청")
    void updateMessageFailInvalidRequest() throws Exception {
        UUID messageId = UUID.randomUUID();

        // newContent가 빈 문자열 → @NotBlank 에러 발생
        MessageUpdateRequest invalidRequest = new MessageUpdateRequest("");

        mockMvc.perform(patch("/api/messages/{messageId}", messageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}