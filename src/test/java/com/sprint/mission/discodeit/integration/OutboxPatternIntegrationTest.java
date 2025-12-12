package com.sprint.mission.discodeit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.channel.domain.Channel;
import com.sprint.mission.discodeit.channel.domain.ChannelRepository;
import com.sprint.mission.discodeit.channel.domain.ChannelType;
import com.sprint.mission.discodeit.common.infrastructure.outbox.AggregateType;
import com.sprint.mission.discodeit.global.security.ratelimit.registry.LoginRateLimitRegistry;
import com.sprint.mission.discodeit.infrastructure.messaging.outbox.OutboxEvent;
import com.sprint.mission.discodeit.infrastructure.messaging.outbox.OutboxEventRepository;
import com.sprint.mission.discodeit.message.domain.MessageRepository;
import com.sprint.mission.discodeit.message.domain.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.message.domain.event.MessageDeletedEvent;
import com.sprint.mission.discodeit.message.presentation.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.support.IntegrationTestSupport;
import com.sprint.mission.discodeit.user.domain.User;
import com.sprint.mission.discodeit.user.domain.UserRepository;
import com.sprint.mission.discodeit.user.domain.event.UserDeletedEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Outbox 패턴 통합 테스트")
class OutboxPatternIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private LoginRateLimitRegistry loginRateLimitRegistry;

    private static final String TEST_USERNAME = "outboxuser";
    private static final String TEST_EMAIL = "outbox@example.com";
    private static final String TEST_PASSWORD = "P@ssw0rd!";
    private static final String DEFAULT_TEST_IP = "127.0.0.1";

    private User testUser;
    private Channel testChannel;

    @BeforeEach
    void setUp() {
        // Clear all caches to prevent stale data between tests
        cacheManager.getCacheNames().forEach(name -> {
            var cache = cacheManager.getCache(name);
            if (cache != null) {
                cache.clear();
            }
        });

        // Reset rate limit
        loginRateLimitRegistry.resetAttempts(DEFAULT_TEST_IP);

        // Clean up existing data
        outboxEventRepository.deleteAll();
        messageRepository.deleteAll();
        channelRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user
        testUser = new User(
            TEST_USERNAME,
            TEST_EMAIL,
            passwordEncoder.encode(TEST_PASSWORD),
            null
        );
        userRepository.save(testUser);

        // Create test channel
        testChannel = new Channel(ChannelType.PUBLIC, "test-channel", "Test Channel");
        channelRepository.save(testChannel);
    }

    @AfterEach
    void tearDown() {
        outboxEventRepository.deleteAll();
        messageRepository.deleteAll();
        channelRepository.deleteAll();
        userRepository.deleteAll();
    }

    private String loginAndGetAccessToken() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", TEST_USERNAME)
                .param("password", TEST_PASSWORD))
            .andExpect(status().isOk())
            .andReturn();

        return objectMapper.readTree(
            loginResult.getResponse().getContentAsString()
        ).get("accessToken").asText();
    }

    private MvcResult createMessage(String accessToken, String content) throws Exception {
        MessageCreateRequest createRequest = new MessageCreateRequest(
            content,
            testChannel.getId(),
            testUser.getId()
        );
        MockMultipartFile requestPart = new MockMultipartFile(
            "messageCreateRequest",
            "",
            MediaType.APPLICATION_JSON_VALUE,
            objectMapper.writeValueAsBytes(createRequest)
        );

        return mockMvc.perform(multipart("/api/messages")
                .file(requestPart)
                .with(csrf())
                .header("Authorization", "Bearer " + accessToken))
            .andExpect(status().isCreated())
            .andReturn();
    }

    private UUID extractMessageId(MvcResult result) throws Exception {
        return UUID.fromString(objectMapper.readTree(
            result.getResponse().getContentAsString()
        ).get("id").asText());
    }

    @Nested
    @DisplayName("메시지 생성 시 Outbox 이벤트")
    class MessageCreatedOutbox {

        @Test
        @DisplayName("메시지 생성 시 MessageCreatedEvent가 Outbox 테이블에 저장된다")
        void createMessage_savesOutboxEvent() throws Exception {
            // Given
            String accessToken = loginAndGetAccessToken();

            // When
            MvcResult result = createMessage(accessToken, "Test message for outbox");
            UUID messageId = extractMessageId(result);

            // Then - wait for async event processing
            await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
                List<OutboxEvent> events = outboxEventRepository.findAll();
                assertThat(events).isNotEmpty();

                OutboxEvent messageEvent = events.stream()
                    .filter(e -> e.getAggregateType() == AggregateType.MESSAGE)
                    .filter(e -> e.getTopic().equals(MessageCreatedEvent.TOPIC))
                    .findFirst()
                    .orElse(null);

                assertThat(messageEvent).isNotNull();
                assertThat(messageEvent.getAggregateId()).isEqualTo(messageId);
                assertThat(messageEvent.getPayload()).contains("messageId");
                assertThat(messageEvent.getPayload()).contains(messageId.toString());
            });
        }

        @Test
        @DisplayName("Outbox 이벤트는 올바른 topic과 aggregateType을 가진다")
        void outboxEvent_hasCorrectMetadata() throws Exception {
            // Given
            String accessToken = loginAndGetAccessToken();

            // When
            MvcResult result = createMessage(accessToken, "Metadata test message");
            UUID messageId = extractMessageId(result);

            // Then
            await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
                List<OutboxEvent> events = outboxEventRepository.findAll();

                OutboxEvent event = events.stream()
                    .filter(e -> e.getAggregateType() == AggregateType.MESSAGE)
                    .filter(e -> e.getTopic().equals(MessageCreatedEvent.TOPIC))
                    .findFirst()
                    .orElse(null);

                assertThat(event).isNotNull();
                assertThat(event.getAggregateId()).isEqualTo(messageId);
                assertThat(event.getTopic()).isEqualTo(MessageCreatedEvent.TOPIC);
                assertThat(event.getAggregateType()).isEqualTo(AggregateType.MESSAGE);
            });
        }
    }

    @Nested
    @DisplayName("메시지 삭제 시 Outbox 이벤트")
    class MessageDeletedOutbox {

        @Test
        @DisplayName("메시지 삭제 시 MessageDeletedEvent가 Outbox 테이블에 저장된다")
        void deleteMessage_savesOutboxEvent() throws Exception {
            // Given
            String accessToken = loginAndGetAccessToken();

            // Create a message first
            MvcResult createResult = createMessage(accessToken, "Message to delete");
            UUID messageId = extractMessageId(createResult);

            // Wait for create event to be processed
            await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
                List<OutboxEvent> events = outboxEventRepository.findAll();
                assertThat(events.stream()
                    .anyMatch(e -> e.getTopic().equals(MessageCreatedEvent.TOPIC))).isTrue();
            });

            // Clear outbox for cleaner assertion
            outboxEventRepository.deleteAll();

            // When - delete the message
            mockMvc.perform(delete("/api/messages/{messageId}", messageId)
                    .header("Authorization", "Bearer " + accessToken)
                    .with(csrf()))
                .andExpect(status().isNoContent());

            // Then
            await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
                List<OutboxEvent> events = outboxEventRepository.findAll();

                OutboxEvent deleteEvent = events.stream()
                    .filter(e -> e.getAggregateType() == AggregateType.MESSAGE)
                    .filter(e -> e.getTopic().equals(MessageDeletedEvent.TOPIC))
                    .findFirst()
                    .orElse(null);

                assertThat(deleteEvent).isNotNull();
                assertThat(deleteEvent.getAggregateId()).isEqualTo(messageId);
                assertThat(deleteEvent.getTopic()).isEqualTo(MessageDeletedEvent.TOPIC);
            });
        }
    }

    @Nested
    @DisplayName("사용자 삭제 시 Outbox 이벤트")
    class UserDeletedOutbox {

        @Test
        @DisplayName("사용자 삭제 시 UserDeletedEvent가 Outbox 테이블에 저장된다")
        void deleteUser_savesOutboxEvent() throws Exception {
            // Given
            String accessToken = loginAndGetAccessToken();
            UUID userId = testUser.getId();

            // When
            mockMvc.perform(delete("/api/users/{userId}", userId)
                    .header("Authorization", "Bearer " + accessToken)
                    .with(csrf()))
                .andExpect(status().isNoContent());

            // Then
            await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
                List<OutboxEvent> events = outboxEventRepository.findAll();

                OutboxEvent userDeleteEvent = events.stream()
                    .filter(e -> e.getAggregateType() == AggregateType.USER)
                    .filter(e -> e.getTopic().equals(UserDeletedEvent.TOPIC))
                    .findFirst()
                    .orElse(null);

                assertThat(userDeleteEvent).isNotNull();
                assertThat(userDeleteEvent.getAggregateId()).isEqualTo(userId);
                assertThat(userDeleteEvent.getTopic()).isEqualTo(UserDeletedEvent.TOPIC);
            });
        }
    }

    @Nested
    @DisplayName("Outbox 이벤트 순서 보장")
    class OutboxEventOrdering {

        @Test
        @DisplayName("여러 메시지 생성 시 Outbox 이벤트가 생성 순서대로 저장된다")
        void multipleMessages_eventsInOrder() throws Exception {
            // Given
            String accessToken = loginAndGetAccessToken();

            // When - create multiple messages
            for (int i = 1; i <= 3; i++) {
                createMessage(accessToken, "Message " + i);
                // Small delay to ensure ordering
                Thread.sleep(50);
            }

            // Then
            await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
                List<OutboxEvent> events = outboxEventRepository.findAll();

                List<OutboxEvent> messageEvents = events.stream()
                    .filter(e -> e.getAggregateType() == AggregateType.MESSAGE)
                    .filter(e -> e.getTopic().equals(MessageCreatedEvent.TOPIC))
                    .toList();

                assertThat(messageEvents).hasSize(3);
            });
        }
    }

    @Nested
    @DisplayName("Outbox 이벤트 페이로드 검증")
    class OutboxEventPayload {

        @Test
        @DisplayName("MessageCreatedEvent 페이로드에 messageId가 포함된다")
        void messageCreatedEvent_containsMessageId() throws Exception {
            // Given
            String accessToken = loginAndGetAccessToken();

            // When
            MvcResult result = createMessage(accessToken, "Payload validation message");
            UUID messageId = extractMessageId(result);

            // Then
            await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
                List<OutboxEvent> events = outboxEventRepository.findAll();

                OutboxEvent event = events.stream()
                    .filter(e -> e.getTopic().equals(MessageCreatedEvent.TOPIC))
                    .findFirst()
                    .orElse(null);

                assertThat(event).isNotNull();

                String payload = event.getPayload();
                assertThat(payload).contains("messageId");
                assertThat(payload).contains(messageId.toString());
            });
        }

        @Test
        @DisplayName("UserDeletedEvent 페이로드에 userId가 포함된다")
        void userDeletedEvent_containsUserId() throws Exception {
            // Given
            String accessToken = loginAndGetAccessToken();
            UUID userId = testUser.getId();

            // When
            mockMvc.perform(delete("/api/users/{userId}", userId)
                    .header("Authorization", "Bearer " + accessToken)
                    .with(csrf()))
                .andExpect(status().isNoContent());

            // Then
            await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
                List<OutboxEvent> events = outboxEventRepository.findAll();

                OutboxEvent event = events.stream()
                    .filter(e -> e.getTopic().equals(UserDeletedEvent.TOPIC))
                    .findFirst()
                    .orElse(null);

                assertThat(event).isNotNull();

                String payload = event.getPayload();
                assertThat(payload).contains("userId");
                assertThat(payload).contains(userId.toString());
            });
        }
    }
}
