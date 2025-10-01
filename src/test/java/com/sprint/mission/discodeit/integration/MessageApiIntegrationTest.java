package com.sprint.mission.discodeit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.message.MessageResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
class MessageApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChannelRepository channelRepository;

    // 헬퍼 메서드: 테스트용 사용자 생성
    private User createTestUser(String username, String email) {
        User user = new User(username, "password123", username, email, null);
        return userRepository.save(user);
    }

    // 헬퍼 메서드: 테스트용 채널 생성
    private Channel createTestChannel(String name, String description) {
        Channel channel = new Channel(name, description);
        return channelRepository.save(channel);
    }

    // 헬퍼 메서드: 메시지 생성
    private MessageResponse createMessage(String content, UUID authorId, UUID channelId) throws Exception {
        MessageCreateRequest request = MessageCreateRequest.builder()
                .content(content)
                .authorId(authorId)
                .channelId(channelId)
                .build();

        MockMultipartFile requestPart = new MockMultipartFile(
                "messageCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        String response = mockMvc.perform(multipart("/api/messages")
                        .file(requestPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        return objectMapper.readValue(response, MessageResponse.class);
    }

    @Test
    @DisplayName("1.1 메시지 생성 후 조회 성공")
    void createMessageAndGet_success() throws Exception {
        // given
        User author = createTestUser("author", "author@example.com");
        Channel channel = createTestChannel("test-channel", "Test Channel");

        // when
        MessageResponse created = createMessage("안녕하세요!", author.getId(), channel.getId());

        // then
        mockMvc.perform(get("/api/messages")
                        .param("channelId", channel.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(created.getId().toString()))
                .andExpect(jsonPath("$.content[0].content").value("안녕하세요!"))
                .andExpect(jsonPath("$.content[0].author.username").value("author"));

        assertThat(messageRepository.findById(created.getId())).isPresent();
    }

    @Test
    @DisplayName("1.2 빈 내용으로 메시지 생성 실패")
    void createMessageWithEmptyContent_failure() throws Exception {
        // given
        User author = createTestUser("author", "author@example.com");
        Channel channel = createTestChannel("test-channel", "Test Channel");

        MessageCreateRequest request = MessageCreateRequest.builder()
                .content("")
                .authorId(author.getId())
                .channelId(channel.getId())
                .build();

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
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("요청 데이터 유효성 검사에 실패했습니다"));

        // then
        assertThat(messageRepository.findByChannelId(channel.getId())).isEmpty();
    }

    @Test
    @DisplayName("1.3 존재하지 않는 사용자로 메시지 생성 실패")
    void createMessageWithNonExistentUser_failure() throws Exception {
        // given
        UUID nonExistentUserId = UUID.randomUUID();
        Channel channel = createTestChannel("test-channel", "Test Channel");

        MessageCreateRequest request = MessageCreateRequest.builder()
                .content("메시지 내용")
                .authorId(nonExistentUserId)
                .channelId(channel.getId())
                .build();

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
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.name()));

        assertThat(messageRepository.findByChannelId(channel.getId())).isEmpty();
    }

    @Test
    @DisplayName("2.1 메시지 생성 후 수정 성공")
    void createMessageAndUpdate_success() throws Exception {
        // given
        User author = createTestUser("author", "author@example.com");
        Channel channel = createTestChannel("test", "Test Channel");
        MessageResponse created = createMessage("원본 내용", author.getId(), channel.getId());

        MessageUpdateRequest updateRequest = MessageUpdateRequest.builder()
                .content("수정된 내용")
                .authorId(author.getId())
                .build();

        MockMultipartFile updatePart = new MockMultipartFile(
                "messageUpdateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(updateRequest)
        );

        // when
        // then
        mockMvc.perform(multipart("/api/messages/{messageId}", created.getId())
                        .file(updatePart)
                        .with(req -> {
                            req.setMethod("PATCH");
                            return req;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("수정된 내용"))
                .andExpect(jsonPath("$.id").value(created.getId().toString()));

        mockMvc.perform(get("/api/messages")
                        .param("channelId", channel.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].content").value("수정된 내용"));

        Message updatedMessage = messageRepository.findById(created.getId()).orElseThrow();
        assertThat(updatedMessage.getContent()).isEqualTo("수정된 내용");
    }

    @Test
    @DisplayName("2.2 다른 사용자가 메시지 수정 시도 실패")
    void updateMessageByDifferentUser_failure() throws Exception {
        // given
        User author = createTestUser("author", "author@example.com");
        User otherUser = createTestUser("other", "other@example.com");
        Channel channel = createTestChannel("test-channel", "Test Channel");
        MessageResponse created = createMessage("원본 내용", author.getId(), channel.getId());

        MessageUpdateRequest updateRequest = MessageUpdateRequest.builder()
                .content("해킹 시도")
                .authorId(otherUser.getId())
                .build();

        MockMultipartFile updatePart = new MockMultipartFile(
                "messageUpdateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(updateRequest)
        );

        // when
        // then
        mockMvc.perform(multipart("/api/messages/{messageId}", created.getId())
                        .file(updatePart)
                        .with(req -> {
                            req.setMethod("PATCH");
                            return req;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorCode.UNAUTHORIZED_MESSAGE_ACCESS.name()));

        Message originalMessage = messageRepository.findById(created.getId()).orElseThrow();
        assertThat(originalMessage.getContent()).isEqualTo("원본 내용");
    }

    @Test
    @DisplayName("2.3 존재하지 않는 메시지 수정 실패")
    void updateNonExistentMessage_failure() throws Exception {
        // given
        User author = createTestUser("author", "author@example.com");
        UUID nonExistentMessageId = UUID.randomUUID();

        MessageUpdateRequest updateRequest = MessageUpdateRequest.builder()
                .content("수정 내용")
                .authorId(author.getId())
                .build();

        MockMultipartFile updatePart = new MockMultipartFile(
                "messageUpdateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(updateRequest)
        );

        // when
        // then
        mockMvc.perform(multipart("/api/messages/{messageId}", nonExistentMessageId)
                        .file(updatePart)
                        .with(req -> {
                            req.setMethod("PATCH");
                            return req;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.MESSAGE_NOT_FOUND.name()));
    }

    @Test
    @DisplayName("3.1 메시지 생성 후 삭제 성공")
    void createMessageAndDelete_success() throws Exception {
        // given
        User author = createTestUser("author", "author@example.com");
        Channel channel = createTestChannel("test-channel", "Test Channel");
        MessageResponse created = createMessage("삭제될 메시지", author.getId(), channel.getId());

        // when
        mockMvc.perform(delete("/api/messages/{messageId}", created.getId())
                        .param("authorId", author.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.id").value(created.getId().toString()));

        // then
        mockMvc.perform(get("/api/messages")
                        .param("channelId", channel.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));

        assertThat(messageRepository.findById(created.getId())).isEmpty();
    }

    @Test
    @DisplayName("3.2 다른 사용자가 메시지 삭제 시도 실패")
    void deleteMessageByDifferentUser_failure() throws Exception {
        // given
        User author = createTestUser("author", "author@example.com");
        User otherUser = createTestUser("other", "other@example.com");
        Channel channel = createTestChannel("test-channel", "Test Channel");
        MessageResponse created = createMessage("삭제 시도될 메시지", author.getId(), channel.getId());

        // when
        // then
        mockMvc.perform(delete("/api/messages/{messageId}", created.getId())
                        .param("authorId", otherUser.getId().toString()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorCode.UNAUTHORIZED_MESSAGE_ACCESS.name()));

        assertThat(messageRepository.findById(created.getId())).isPresent();
        mockMvc.perform(get("/api/messages")
                        .param("channelId", channel.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    @DisplayName("3.3 존재하지 않는 메시지 삭제 실패")
    void deleteNonExistentMessage_failure() throws Exception {
        // given
        User author = createTestUser("author", "author@example.com");
        UUID nonExistentMessageId = UUID.randomUUID();

        // when
        // then
        mockMvc.perform(delete("/api/messages/{messageId}", nonExistentMessageId)
                        .param("authorId", author.getId().toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.MESSAGE_NOT_FOUND.name()));
    }

    @Test
    @DisplayName("4.1 여러 메시지 생성 후 채널별 목록 조회")
    void createMultipleMessagesAndGetByChannel_success() throws Exception {
        // given
        User author1 = createTestUser("author1", "author1@example.com");
        User author2 = createTestUser("author2", "author2@example.com");
        Channel channel1 = createTestChannel("channel1", "Channel 1");
        Channel channel2 = createTestChannel("channel2", "Channel 2");

        createMessage("채널1 메시지1", author1.getId(), channel1.getId());
        createMessage("채널1 메시지2", author2.getId(), channel1.getId());
        createMessage("채널2 메시지1", author1.getId(), channel2.getId());

        // when
        // then
        mockMvc.perform(get("/api/messages")
                        .param("channelId", channel1.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));

        mockMvc.perform(get("/api/messages")
                        .param("channelId", channel2.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].content").value("채널2 메시지1"));

        Channel emptyChannel = createTestChannel("empty", "Empty Channel");
        mockMvc.perform(get("/api/messages")
                        .param("channelId", emptyChannel.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    @Test
    @DisplayName("5.1 메시지 생성 후 수정 조회 삭제 순차 통합테스트")
    void messageLifecycleScenario() throws Exception {
        // given
        User author = createTestUser("author", "author@example.com");
        Channel channel = createTestChannel("test-channel", "Test Channel");

        MessageResponse created = createMessage("초기 메시지", author.getId(), channel.getId());
        assertThat(messageRepository.findByChannelId(channel.getId())).hasSize(1);

        MessageUpdateRequest firstUpdate = MessageUpdateRequest.builder()
                .content("첫 번째 수정")
                .authorId(author.getId())
                .build();

        MockMultipartFile firstUpdatePart = new MockMultipartFile(
                "messageUpdateRequest", "", MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(firstUpdate)
        );

        mockMvc.perform(multipart("/api/messages/{messageId}", created.getId())
                        .file(firstUpdatePart)
                        .with(req -> {
                            req.setMethod("PATCH");
                            return req;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("첫 번째 수정"));

        MessageUpdateRequest secondUpdate = MessageUpdateRequest.builder()
                .content("최종 수정")
                .authorId(author.getId())
                .build();

        MockMultipartFile secondUpdatePart = new MockMultipartFile(
                "messageUpdateRequest", "", MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(secondUpdate)
        );

        mockMvc.perform(multipart("/api/messages/{messageId}", created.getId())
                        .file(secondUpdatePart)
                        .with(req -> {
                            req.setMethod("PATCH");
                            return req;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("최종 수정"));

        mockMvc.perform(get("/api/messages")
                        .param("channelId", channel.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].content").value("최종 수정"));

        mockMvc.perform(delete("/api/messages/{messageId}", created.getId())
                        .param("authorId", author.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        mockMvc.perform(get("/api/messages")
                        .param("channelId", channel.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0));

        assertThat(messageRepository.findByChannelId(channel.getId())).isEmpty();
    }
}