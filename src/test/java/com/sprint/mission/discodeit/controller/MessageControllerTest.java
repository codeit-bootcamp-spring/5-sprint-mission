package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.message.MessageDeleteResponse;
import com.sprint.mission.discodeit.dto.response.message.MessageResponse;
import com.sprint.mission.discodeit.exception.message.UnauthorizedMessageAccessException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.service.MessageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MessageController.class)
public class MessageControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private MessageService messageService;
    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;


    @Test
    @DisplayName("메시지 생성 성공")
    void createMessage_success() throws Exception {
        // given
        UUID authorId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();

        MessageCreateRequest request = MessageCreateRequest.builder()
                .authorId(authorId)
                .channelId(channelId)
                .content("test")
                .build();

        MessageResponse response = MessageResponse.builder()
                .id(UUID.randomUUID())
                .authorId(authorId)
                .channelId(channelId)
                .content("test")
                .createdAt(Instant.now())
                .build();

        given(messageService.create(any(MessageCreateRequest.class))).willReturn(response);

        MockMultipartFile requestPart = new MockMultipartFile(
                "messageCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        // when
        // then
        mockMvc.perform(multipart("/api/messages")
                        .file(requestPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("test"));
    }

    @Test
    @DisplayName("부적절한 요청으로 메시지 생성 실패")
    void createMessage_fail_invalidContent() throws Exception {
        // given
        MessageCreateRequest invalidRequest = MessageCreateRequest.builder()
                .authorId(UUID.randomUUID())
                .channelId(UUID.randomUUID())
                .content("")
                .build();

        MockMultipartFile requestPart = new MockMultipartFile(
                "messageCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(invalidRequest)
        );

        // when
        // then
        mockMvc.perform(multipart("/api/messages")
                        .file(requestPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("존재하지 않는 유저로 메시지 생성 실패")
    void createMessage_fail_invalidUser() throws Exception {
        // given
        UUID invalidUserId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();

        MessageCreateRequest request = MessageCreateRequest.builder()
                .authorId(invalidUserId)
                .channelId(channelId)
                .content("Hello world")
                .build();

        MockMultipartFile requestPart = new MockMultipartFile(
                "messageCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        given(messageService.create(any(MessageCreateRequest.class)))
                .willThrow(new UserNotFoundException());

        // when
        // then
        mockMvc.perform(multipart("/api/messages")
                        .file(requestPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("메시지 수정 성공")
    void updateMessage_success() throws Exception {
        // given
        UUID messageId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        MessageUpdateRequest request = MessageUpdateRequest.builder()
                .authorId(authorId)
                .content("Updated content")
                .build();

        MessageResponse response = MessageResponse.builder()
                .id(messageId)
                .authorId(authorId)
                .channelId(UUID.randomUUID())
                .content("Updated content")
                .updatedAt(Instant.now())
                .build();

        given(messageService.updateMessage(eq(messageId), any(MessageUpdateRequest.class))).willReturn(response);

        MockMultipartFile requestPart = new MockMultipartFile(
                "messageCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        // when
        // then
        mockMvc.perform(multipart("/api/messages/{messageId}", messageId)
                        .file(requestPart)
                        .with(req -> { req.setMethod("PATCH"); return req; })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated content"));
    }

    @Test
    @DisplayName("빈 내용으로 메시지 수정 실패")
    void updateMessage_fail_invalidContent() throws Exception {
        // given
        UUID messageId = UUID.randomUUID();

        MessageUpdateRequest invalidRequest = MessageUpdateRequest.builder()
                .authorId(UUID.randomUUID())
                .content("")
                .build();

        MockMultipartFile requestPart = new MockMultipartFile(
                "messageCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(invalidRequest)
        );

        // when
        // then
        mockMvc.perform(multipart("/api/messages/{messageId}", messageId)
                        .file(requestPart)
                        .with(req -> { req.setMethod("PATCH"); return req; })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("작성자가 아닌 유저가 메시지 수정 실패")
    void updateMessage_fail_unauthorizedUser() throws Exception {
        // given
        UUID messageId = UUID.randomUUID();
        UUID wrongUserId = UUID.randomUUID();

        MessageUpdateRequest request = MessageUpdateRequest.builder()
                .authorId(wrongUserId)
                .content("Hacked content")
                .build();

        MockMultipartFile requestPart = new MockMultipartFile(
                "messageCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        given(messageService.updateMessage(eq(messageId), any(MessageUpdateRequest.class)))
                .willThrow(UnauthorizedMessageAccessException.withDetails(messageId, wrongUserId, UUID.randomUUID(), "update"));

        // when
        // then
        mockMvc.perform(multipart("/api/messages/{messageId}", messageId)
                        .file(requestPart)
                        .with(req -> { req.setMethod("PATCH"); return req; })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("메시지 삭제 성공")
    void deleteMessage_success() throws Exception {
        // given
        UUID messageId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        MessageDeleteResponse response = MessageDeleteResponse.builder()
                .id(messageId)
                .authorId(authorId)
                .success(true)
                .build();

        given(messageService.deleteMessage(eq(messageId), eq(authorId))).willReturn(response);

        // when
        // then
        mockMvc.perform(delete("/api/messages/{messageId}", messageId)
                        .param("authorId", authorId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("권한 없는 유저가 메시지 삭제 실패")
    void deleteMessage_fail_wrongAuthor() throws Exception {
        // given
        UUID messageId = UUID.randomUUID();
        UUID wrongAuthorId = UUID.randomUUID();

        given(messageService.deleteMessage(eq(messageId), eq(wrongAuthorId)))
                .willThrow(UnauthorizedMessageAccessException.withDetails(messageId, wrongAuthorId, UUID.randomUUID(), "delete"));

        // when
        // then
        mockMvc.perform(delete("/api/messages/{messageId}", messageId)
                        .param("authorId", wrongAuthorId.toString()))
                .andExpect(status().isUnauthorized());
    }

}
