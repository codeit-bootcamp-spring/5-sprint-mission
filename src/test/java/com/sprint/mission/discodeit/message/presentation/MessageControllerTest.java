package com.sprint.mission.discodeit.message.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.common.presentation.dto.PaginationRequest;
import com.sprint.mission.discodeit.common.presentation.dto.PaginationResponse;
import com.sprint.mission.discodeit.global.error.GlobalExceptionHandler;
import com.sprint.mission.discodeit.global.security.jwt.JwtAuthenticationFilter;
import com.sprint.mission.discodeit.global.security.jwt.JwtLogoutHandler;
import com.sprint.mission.discodeit.global.security.ratelimit.LoginRateLimitFilter;
import com.sprint.mission.discodeit.message.application.MessageService;
import com.sprint.mission.discodeit.message.domain.exception.EmptyMessageContentException;
import com.sprint.mission.discodeit.message.domain.exception.MessageNotFoundException;
import com.sprint.mission.discodeit.message.presentation.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.message.presentation.dto.MessageDto;
import com.sprint.mission.discodeit.message.presentation.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.support.TestSecurityConfig;
import com.sprint.mission.discodeit.user.domain.Role;
import com.sprint.mission.discodeit.user.presentation.dto.UserDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
    controllers = MessageController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {
            JwtAuthenticationFilter.class,
            LoginRateLimitFilter.class,
            JwtLogoutHandler.class
        }
    )
)
@Import({GlobalExceptionHandler.class, TestSecurityConfig.class})
@DisplayName("MessageController 테스트")
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MessageService messageService;

    private static final UUID TEST_MESSAGE_ID = UUID.randomUUID();
    private static final UUID TEST_CHANNEL_ID = UUID.randomUUID();
    private static final UUID TEST_AUTHOR_ID = UUID.randomUUID();
    private static final String TEST_CONTENT = "Hello, world!";
    private static final Instant NOW = Instant.now();

    private MessageDto createMessageDto() {
        UserDto author = new UserDto(
            TEST_AUTHOR_ID, "testuser", "test@test.com", null, true, Role.USER
        );
        return new MessageDto(
            TEST_MESSAGE_ID, NOW, NOW, TEST_CONTENT, TEST_CHANNEL_ID, author, List.of()
        );
    }

    @Nested
    @DisplayName("POST /api/messages")
    class Create {

        @Test
        @WithMockUser
        @DisplayName("유효한 요청으로 메시지 생성 시 201 반환")
        void create_withValidRequest_returns201() throws Exception {
            // given
            MessageCreateRequest request = new MessageCreateRequest(
                TEST_CONTENT, TEST_CHANNEL_ID, TEST_AUTHOR_ID
            );
            MessageDto messageDto = createMessageDto();

            MockMultipartFile requestPart = new MockMultipartFile(
                "messageCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
            );

            given(messageService.create(any(MessageCreateRequest.class), any()))
                .willReturn(messageDto);

            // when & then
            mockMvc.perform(multipart("/api/messages")
                    .file(requestPart))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(TEST_MESSAGE_ID.toString()))
                .andExpect(jsonPath("$.content").value(TEST_CONTENT))
                .andExpect(jsonPath("$.channelId").value(TEST_CHANNEL_ID.toString()))
                .andExpect(jsonPath("$.author.id").value(TEST_AUTHOR_ID.toString()));
        }

        @Test
        @WithMockUser
        @DisplayName("첨부파일과 함께 메시지 생성 시 201 반환")
        void create_withAttachments_returns201() throws Exception {
            // given
            MessageCreateRequest request = new MessageCreateRequest(
                TEST_CONTENT, TEST_CHANNEL_ID, TEST_AUTHOR_ID
            );
            MessageDto messageDto = createMessageDto();

            MockMultipartFile requestPart = new MockMultipartFile(
                "messageCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
            );
            MockMultipartFile attachment = new MockMultipartFile(
                "attachments",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "test content".getBytes()
            );

            given(messageService.create(any(MessageCreateRequest.class), any()))
                .willReturn(messageDto);

            // when & then
            mockMvc.perform(multipart("/api/messages")
                    .file(requestPart)
                    .file(attachment))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(TEST_MESSAGE_ID.toString()));
        }

        @Test
        @DisplayName("인증 없이 메시지 생성 시 403 반환")
        void create_withoutAuth_returns403() throws Exception {
            // given
            MessageCreateRequest request = new MessageCreateRequest(
                TEST_CONTENT, TEST_CHANNEL_ID, TEST_AUTHOR_ID
            );

            MockMultipartFile requestPart = new MockMultipartFile(
                "messageCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
            );

            // when & then
            mockMvc.perform(multipart("/api/messages")
                    .file(requestPart))
                .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser
        @DisplayName("channelId 없이 생성 시 400 반환")
        void create_withoutChannelId_returns400() throws Exception {
            // given
            MessageCreateRequest request = new MessageCreateRequest(
                TEST_CONTENT, null, TEST_AUTHOR_ID
            );

            MockMultipartFile requestPart = new MockMultipartFile(
                "messageCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
            );

            // when & then
            mockMvc.perform(multipart("/api/messages")
                    .file(requestPart))
                .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser
        @DisplayName("authorId 없이 생성 시 400 반환")
        void create_withoutAuthorId_returns400() throws Exception {
            // given
            MessageCreateRequest request = new MessageCreateRequest(
                TEST_CONTENT, TEST_CHANNEL_ID, null
            );

            MockMultipartFile requestPart = new MockMultipartFile(
                "messageCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
            );

            // when & then
            mockMvc.perform(multipart("/api/messages")
                    .file(requestPart))
                .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser
        @DisplayName("내용과 첨부파일 모두 없이 생성 시 400 반환")
        void create_withoutContentAndAttachments_returns400() throws Exception {
            // given
            MessageCreateRequest request = new MessageCreateRequest(
                null, TEST_CHANNEL_ID, TEST_AUTHOR_ID
            );

            MockMultipartFile requestPart = new MockMultipartFile(
                "messageCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
            );

            given(messageService.create(any(MessageCreateRequest.class), any()))
                .willThrow(new EmptyMessageContentException());

            // when & then
            mockMvc.perform(multipart("/api/messages")
                    .file(requestPart))
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/messages")
    class FindAllByChannelId {

        @Test
        @WithMockUser
        @DisplayName("채널별 메시지 조회 시 200 반환")
        void findAllByChannelId_withValidRequest_returns200() throws Exception {
            // given
            MessageDto messageDto = createMessageDto();
            PaginationResponse<MessageDto> response = new PaginationResponse<>(
                List.of(messageDto), null, 50, false
            );

            given(messageService.findAllByChannelId(eq(TEST_CHANNEL_ID), any(), any()))
                .willReturn(response);

            // when & then
            mockMvc.perform(get("/api/messages")
                    .param("channelId", TEST_CHANNEL_ID.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(TEST_MESSAGE_ID.toString()))
                .andExpect(jsonPath("$.hasNext").value(false));
        }

        @Test
        @WithMockUser
        @DisplayName("커서와 함께 메시지 조회 시 200 반환")
        void findAllByChannelId_withCursor_returns200() throws Exception {
            // given
            MessageDto messageDto = createMessageDto();
            Instant nextCursor = NOW.minusSeconds(60);
            PaginationResponse<MessageDto> response = new PaginationResponse<>(
                List.of(messageDto), nextCursor, 50, true
            );

            given(messageService.findAllByChannelId(eq(TEST_CHANNEL_ID), any(), any()))
                .willReturn(response);

            // when & then
            mockMvc.perform(get("/api/messages")
                    .param("channelId", TEST_CHANNEL_ID.toString())
                    .param("cursor", NOW.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.hasNext").value(true))
                .andExpect(jsonPath("$.nextCursor").exists());
        }

        @Test
        @WithMockUser
        @DisplayName("메시지가 없을 때 빈 응답 반환")
        void findAllByChannelId_withNoMessages_returnsEmptyResponse() throws Exception {
            // given
            PaginationResponse<MessageDto> emptyResponse = PaginationResponse.empty();

            given(messageService.findAllByChannelId(eq(TEST_CHANNEL_ID), any(), any()))
                .willReturn(emptyResponse);

            // when & then
            mockMvc.perform(get("/api/messages")
                    .param("channelId", TEST_CHANNEL_ID.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.hasNext").value(false));
        }

        @Test
        @DisplayName("인증 없이 메시지 조회 시 403 반환")
        void findAllByChannelId_withoutAuth_returns403() throws Exception {
            // when & then
            mockMvc.perform(get("/api/messages")
                    .param("channelId", TEST_CHANNEL_ID.toString()))
                .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser
        @DisplayName("channelId 없이 조회 시 400 반환")
        void findAllByChannelId_withoutChannelId_returns400() throws Exception {
            // when & then
            mockMvc.perform(get("/api/messages"))
                .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser
        @DisplayName("page가 음수일 때 400 반환")
        void findAllByChannelId_withNegativePage_returns400() throws Exception {
            // given
            int invalidPage = -1;
            int validSize = PaginationRequest.DEFAULT_SIZE;

            // when & then
            mockMvc.perform(get("/api/messages")
                    .param("channelId", TEST_CHANNEL_ID.toString())
                    .param("page", String.valueOf(invalidPage))
                    .param("size", String.valueOf(validSize)))
                .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser
        @DisplayName("size가 0일 때 400 반환")
        void findAllByChannelId_withZeroSize_returns400() throws Exception {
            // given
            int validPage = PaginationRequest.DEFAULT_PAGE;
            int invalidSize = 0;

            // when & then
            mockMvc.perform(get("/api/messages")
                    .param("channelId", TEST_CHANNEL_ID.toString())
                    .param("page", String.valueOf(validPage))
                    .param("size", String.valueOf(invalidSize)))
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PATCH /api/messages/{messageId}")
    class Update {

        @Test
        @WithMockUser
        @DisplayName("유효한 요청으로 메시지 수정 시 200 반환")
        void update_withValidRequest_returns200() throws Exception {
            // given
            String newContent = "Updated content";
            MessageUpdateRequest request = new MessageUpdateRequest(newContent);
            MessageDto updatedMessage = new MessageDto(
                TEST_MESSAGE_ID, NOW, Instant.now(), newContent, TEST_CHANNEL_ID,
                new UserDto(TEST_AUTHOR_ID, "testuser", "test@test.com", null, true, Role.USER),
                List.of()
            );

            given(messageService.update(eq(TEST_MESSAGE_ID), any(MessageUpdateRequest.class)))
                .willReturn(updatedMessage);

            // when & then
            mockMvc.perform(patch("/api/messages/{messageId}", TEST_MESSAGE_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(TEST_MESSAGE_ID.toString()))
                .andExpect(jsonPath("$.content").value(newContent));
        }

        @Test
        @DisplayName("인증 없이 메시지 수정 시 403 반환")
        void update_withoutAuth_returns403() throws Exception {
            // given
            MessageUpdateRequest request = new MessageUpdateRequest("Updated content");

            // when & then
            mockMvc.perform(patch("/api/messages/{messageId}", TEST_MESSAGE_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser
        @DisplayName("존재하지 않는 메시지 수정 시 404 반환")
        void update_withNonExistingMessage_returns404() throws Exception {
            // given
            UUID nonExistingId = UUID.randomUUID();
            MessageUpdateRequest request = new MessageUpdateRequest("Updated content");

            given(messageService.update(eq(nonExistingId), any(MessageUpdateRequest.class)))
                .willThrow(new MessageNotFoundException(nonExistingId));

            // when & then
            mockMvc.perform(patch("/api/messages/{messageId}", nonExistingId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser
        @DisplayName("작성자가 아닌 사용자가 수정 시 403 반환")
        void update_withNonAuthor_returns403() throws Exception {
            // given
            MessageUpdateRequest request = new MessageUpdateRequest("Updated content");

            given(messageService.update(eq(TEST_MESSAGE_ID), any(MessageUpdateRequest.class)))
                .willThrow(new AuthorizationDeniedException(
                    "Access Denied", new AuthorizationDecision(false)
                ));

            // when & then
            mockMvc.perform(patch("/api/messages/{messageId}", TEST_MESSAGE_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser
        @DisplayName("빈 내용으로 수정 시 (첨부파일 없음) 400 반환")
        void update_withEmptyContentAndNoAttachments_returns400() throws Exception {
            // given
            MessageUpdateRequest request = new MessageUpdateRequest("   ");

            given(messageService.update(eq(TEST_MESSAGE_ID), any(MessageUpdateRequest.class)))
                .willThrow(new EmptyMessageContentException());

            // when & then
            mockMvc.perform(patch("/api/messages/{messageId}", TEST_MESSAGE_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("DELETE /api/messages/{messageId}")
    class DeleteById {

        @Test
        @WithMockUser
        @DisplayName("유효한 요청으로 메시지 삭제 시 204 반환")
        void deleteById_withValidRequest_returns204() throws Exception {
            // given
            willDoNothing().given(messageService).deleteById(TEST_MESSAGE_ID);

            // when & then
            mockMvc.perform(delete("/api/messages/{messageId}", TEST_MESSAGE_ID))
                .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("인증 없이 메시지 삭제 시 403 반환")
        void deleteById_withoutAuth_returns403() throws Exception {
            // when & then
            mockMvc.perform(delete("/api/messages/{messageId}", TEST_MESSAGE_ID))
                .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser
        @DisplayName("존재하지 않는 메시지 삭제 시 404 반환")
        void deleteById_withNonExistingMessage_returns404() throws Exception {
            // given
            UUID nonExistingId = UUID.randomUUID();

            willThrow(new MessageNotFoundException(nonExistingId))
                .given(messageService).deleteById(nonExistingId);

            // when & then
            mockMvc.perform(delete("/api/messages/{messageId}", nonExistingId))
                .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser
        @DisplayName("작성자가 아닌 사용자가 삭제 시 403 반환")
        void deleteById_withNonAuthor_returns403() throws Exception {
            // given
            willThrow(new AuthorizationDeniedException(
                "Access Denied", new AuthorizationDecision(false)
            )).given(messageService).deleteById(TEST_MESSAGE_ID);

            // when & then
            mockMvc.perform(delete("/api/messages/{messageId}", TEST_MESSAGE_ID))
                .andExpect(status().isForbidden());
        }
    }
}
