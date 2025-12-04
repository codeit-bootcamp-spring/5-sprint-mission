package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.common.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.common.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.config.TestSecurityConfig;
import com.sprint.mission.discodeit.domain.controller.MessageController;
import com.sprint.mission.discodeit.domain.dto.message.data.MessageDto;
import com.sprint.mission.discodeit.domain.dto.message.request.MessageCreateRequest;
import com.sprint.mission.discodeit.domain.dto.message.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.domain.dto.pagination.request.Pageable;
import com.sprint.mission.discodeit.domain.dto.pagination.response.PageResponse;
import com.sprint.mission.discodeit.domain.service.MessageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.sprint.mission.discodeit.support.TestFixtures.TEST_EMAIL;
import static com.sprint.mission.discodeit.support.TestFixtures.TEST_USERNAME;
import static com.sprint.mission.discodeit.support.TestFixtures.createFilePart;
import static com.sprint.mission.discodeit.support.TestFixtures.createJsonRequestPart;
import static com.sprint.mission.discodeit.support.TestFixtures.createUserDto;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = MessageController.class, excludeFilters = @ComponentScan.Filter(
    type = FilterType.REGEX, pattern = ".*\\.security\\..*|.*\\.config\\.SecurityConfig"))
@Import({GlobalExceptionHandler.class, TestSecurityConfig.class})
@ActiveProfiles("test")
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MessageService messageService;

    @Test
    @WithMockUser
    @DisplayName("POST /api/messages - 성공: 메시지 생성 (첨부파일 없음)")
    void create_WithoutAttachments_Success() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        String content = "Hello, world!";
        MessageCreateRequest request = new MessageCreateRequest(content, channelId, authorId);
        MessageDto response = new MessageDto(
            UUID.randomUUID(),
            Instant.now(),
            Instant.now(),
            content,
            channelId,
            createUserDto(authorId, TEST_USERNAME, TEST_EMAIL),
            List.of()
        );

        MockMultipartFile requestPart = createJsonRequestPart("messageCreateRequest", request, objectMapper);

        given(messageService.create(any(MessageCreateRequest.class), eq(null)))
            .willReturn(response);

        // when & then
        mockMvc.perform(multipart("/api/messages")
                .file(requestPart)
                .with(csrf()))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.content").value(content))
            .andExpect(jsonPath("$.channelId").value(channelId.toString()))
            .andExpect(jsonPath("$.author.id").value(authorId.toString()))
            .andExpect(jsonPath("$.attachments.length()").value(0));

        then(messageService).should().create(any(MessageCreateRequest.class), eq(null));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/messages - 성공: 메시지 생성 (첨부파일 포함)")
    void create_WithAttachments_Success() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        String content = "Check out this file!";
        MessageCreateRequest request = new MessageCreateRequest(content, channelId, authorId);
        MessageDto response = new MessageDto(
            UUID.randomUUID(),
            Instant.now(),
            Instant.now(),
            content,
            channelId,
            createUserDto(authorId, TEST_USERNAME, TEST_EMAIL),
            List.of()
        );

        MockMultipartFile requestPart = createJsonRequestPart("messageCreateRequest", request, objectMapper);
        MockMultipartFile attachmentPart = createFilePart(
            "attachments", "document.pdf", MediaType.APPLICATION_PDF_VALUE, "test document".getBytes()
        );

        given(messageService.create(any(MessageCreateRequest.class), anyList()))
            .willReturn(response);

        // when & then
        mockMvc.perform(multipart("/api/messages")
                .file(requestPart)
                .file(attachmentPart)
                .with(csrf()))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.content").value(content));

        then(messageService).should().create(any(MessageCreateRequest.class), anyList());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /api/messages - 실패: 잘못된 요청 데이터 (유효성 검증 실패)")
    void create_InvalidData_BadRequest() throws Exception {
        // given - channelId가 null
        MessageCreateRequest request = new MessageCreateRequest("Hello", null, UUID.randomUUID());

        MockMultipartFile requestPart = createJsonRequestPart("messageCreateRequest", request, objectMapper);

        // when & then
        mockMvc.perform(multipart("/api/messages")
                .file(requestPart)
                .with(csrf()))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/messages - 성공: 채널의 메시지 목록 조회")
    void findAllByChannelId_Success() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();
        Instant now = Instant.now();
        List<MessageDto> messages = List.of(
            new MessageDto(
                UUID.randomUUID(),
                now.minusSeconds(3600),
                now.minusSeconds(3600),
                "First message",
                channelId,
                createUserDto(UUID.randomUUID(), "user1", "user1@example.com"),
                List.of()
            ),
            new MessageDto(
                UUID.randomUUID(),
                now,
                now,
                "Second message",
                channelId,
                createUserDto(UUID.randomUUID(), "user2", "user2@example.com"),
                List.of()
            )
        );
        PageResponse<MessageDto> pageResponse = new PageResponse<>(messages, null, 2, false, 2L);

        given(messageService.findAllByChannelId(eq(channelId), isNull(), any(Pageable.class)))
            .willReturn(pageResponse);

        // when & then
        mockMvc.perform(get("/api/messages")
                .param("channelId", channelId.toString())
                .param("page", "0")
                .param("size", "50"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(2))
            .andExpect(jsonPath("$.size").value(2))
            .andExpect(jsonPath("$.hasNext").value(false))
            .andExpect(jsonPath("$.totalElements").value(2));

        then(messageService).should().findAllByChannelId(eq(channelId), isNull(), any(Pageable.class));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/messages - 성공: 커서 기반 페이징")
    void findAllByChannelId_WithCursor_Success() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();
        Instant cursor = Instant.now().minusSeconds(7200);
        PageResponse<MessageDto> pageResponse = new PageResponse<>(List.of(), null, 0, false, 0L);

        given(messageService.findAllByChannelId(eq(channelId), eq(cursor), any(Pageable.class)))
            .willReturn(pageResponse);

        // when & then
        mockMvc.perform(get("/api/messages")
                .param("channelId", channelId.toString())
                .param("cursor", cursor.toString())
                .param("page", "0")
                .param("size", "50"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(0));

        then(messageService).should().findAllByChannelId(eq(channelId), eq(cursor), any(Pageable.class));
    }

    @Test
    @WithMockUser
    @DisplayName("PATCH /api/messages/{messageId} - 성공: 메시지 수정")
    void update_Success() throws Exception {
        // given
        UUID messageId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        String updatedContent = "Updated content";
        MessageUpdateRequest request = new MessageUpdateRequest(updatedContent);
        MessageDto response = new MessageDto(
            messageId,
            Instant.now().minusSeconds(3600),
            Instant.now(),
            updatedContent,
            channelId,
            createUserDto(UUID.randomUUID(), TEST_USERNAME, TEST_EMAIL),
            List.of()
        );

        given(messageService.update(eq(messageId), any(MessageUpdateRequest.class)))
            .willReturn(response);

        // when & then
        mockMvc.perform(patch("/api/messages/{messageId}", messageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").value(updatedContent));

        then(messageService).should().update(eq(messageId), any(MessageUpdateRequest.class));
    }

    @Test
    @WithMockUser
    @DisplayName("PATCH /api/messages/{messageId} - 실패: 존재하지 않는 메시지")
    void update_MessageNotFound() throws Exception {
        // given
        UUID messageId = UUID.randomUUID();
        MessageUpdateRequest request = new MessageUpdateRequest("Updated content");

        given(messageService.update(eq(messageId), any(MessageUpdateRequest.class)))
            .willThrow(new MessageNotFoundException());

        // when & then
        mockMvc.perform(patch("/api/messages/{messageId}", messageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("MESSAGE_NOT_FOUND"));

        then(messageService).should().update(eq(messageId), any(MessageUpdateRequest.class));
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /api/messages/{messageId} - 성공: 메시지 삭제")
    void delete_ById_Success() throws Exception {
        // given
        UUID messageId = UUID.randomUUID();
        willDoNothing().given(messageService).deleteById(messageId);

        // when & then
        mockMvc.perform(delete("/api/messages/{messageId}", messageId)
                .with(csrf()))
            .andExpect(status().isNoContent());

        then(messageService).should().deleteById(messageId);
    }

    @Test
    @WithMockUser
    @DisplayName("DELETE /api/messages/{messageId} - 실패: 존재하지 않는 메시지")
    void delete_ById_MessageNotFound() throws Exception {
        // given
        UUID messageId = UUID.randomUUID();
        willThrow(new MessageNotFoundException()).given(messageService).deleteById(messageId);

        // when & then
        mockMvc.perform(delete("/api/messages/{messageId}", messageId)
                .with(csrf()))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("MESSAGE_NOT_FOUND"));

        then(messageService).should().deleteById(messageId);
    }
}
