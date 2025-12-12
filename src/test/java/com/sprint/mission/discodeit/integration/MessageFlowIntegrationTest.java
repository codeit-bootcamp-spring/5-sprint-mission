package com.sprint.mission.discodeit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.channel.domain.Channel;
import com.sprint.mission.discodeit.channel.domain.ChannelRepository;
import com.sprint.mission.discodeit.channel.domain.ChannelType;
import com.sprint.mission.discodeit.global.security.ratelimit.registry.LoginRateLimitRegistry;
import com.sprint.mission.discodeit.message.domain.MessageRepository;
import com.sprint.mission.discodeit.message.presentation.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.message.presentation.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.readstatus.domain.ReadStatus;
import com.sprint.mission.discodeit.readstatus.domain.ReadStatusRepository;
import com.sprint.mission.discodeit.support.IntegrationTestSupport;
import com.sprint.mission.discodeit.user.domain.User;
import com.sprint.mission.discodeit.user.domain.UserRepository;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("메시지 플로우 통합 테스트")
class MessageFlowIntegrationTest extends IntegrationTestSupport {

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
    private ReadStatusRepository readStatusRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LoginRateLimitRegistry loginRateLimitRegistry;

    @Autowired
    private CacheManager cacheManager;

    private User testUser;
    private User otherUser;
    private Channel publicChannel;

    private static final String TEST_PASSWORD = "P@ssw0rd!";
    private static final String DEFAULT_TEST_IP = "127.0.0.1";

    @BeforeEach
    void setUp() {
        messageRepository.deleteAll();
        readStatusRepository.deleteAll();
        channelRepository.deleteAll();
        userRepository.deleteAll();
        loginRateLimitRegistry.resetAttempts(DEFAULT_TEST_IP);

        // Clear all caches to prevent stale data between tests
        cacheManager.getCacheNames().forEach(name -> {
            var cache = cacheManager.getCache(name);
            if (cache != null) {
                cache.clear();
            }
        });

        testUser = userRepository.save(new User(
            "testuser",
            "test@example.com",
            passwordEncoder.encode(TEST_PASSWORD),
            null
        ));

        otherUser = userRepository.save(new User(
            "otheruser",
            "other@example.com",
            passwordEncoder.encode(TEST_PASSWORD),
            null
        ));

        publicChannel = channelRepository.save(new Channel(
            ChannelType.PUBLIC,
            "test-channel",
            "Test channel description"
        ));

        // ReadStatus 설정 (알림 활성화)
        readStatusRepository.save(new ReadStatus(
            testUser,
            publicChannel,
            Instant.now(),
            true
        ));
        readStatusRepository.save(new ReadStatus(
            otherUser,
            publicChannel,
            Instant.now(),
            true
        ));
    }

    @AfterEach
    void tearDown() {
        messageRepository.deleteAll();
        readStatusRepository.deleteAll();
        channelRepository.deleteAll();
        userRepository.deleteAll();
    }

    private String loginAndGetAccessToken(String username) throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", username)
                .param("password", TEST_PASSWORD))
            .andExpect(status().isOk())
            .andReturn();

        return objectMapper.readTree(
            loginResult.getResponse().getContentAsString()
        ).get("accessToken").asText();
    }

    @Nested
    @DisplayName("메시지 CRUD 전체 플로우")
    class FullMessageFlow {

        @Test
        @DisplayName("메시지 생성 → 조회 → 수정 → 삭제 전체 플로우가 정상적으로 동작한다")
        void fullMessageFlow_success() throws Exception {
            String accessToken = loginAndGetAccessToken("testuser");

            // 1. 메시지 생성
            MessageCreateRequest createRequest = new MessageCreateRequest(
                "Hello, world!",
                publicChannel.getId(),
                testUser.getId()
            );
            MockMultipartFile requestPart = new MockMultipartFile(
                "messageCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(createRequest)
            );

            MvcResult createResult = mockMvc.perform(multipart("/api/messages")
                    .file(requestPart)
                    .with(csrf())
                    .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Hello, world!"))
                .andExpect(jsonPath("$.channelId").value(publicChannel.getId().toString()))
                .andExpect(jsonPath("$.author.id").value(testUser.getId().toString()))
                .andReturn();

            String messageId = objectMapper.readTree(
                createResult.getResponse().getContentAsString()
            ).get("id").asText();

            // 2. 메시지 조회
            mockMvc.perform(get("/api/messages")
                    .param("channelId", publicChannel.getId().toString())
                    .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(messageId))
                .andExpect(jsonPath("$.content[0].content").value("Hello, world!"));

            // 3. 메시지 수정 (작성자 본인만 가능)
            MessageUpdateRequest updateRequest = new MessageUpdateRequest("Updated message");

            mockMvc.perform(patch("/api/messages/{messageId}", messageId)
                    .with(csrf())
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated message"));

            // 4. 수정된 메시지 조회
            mockMvc.perform(get("/api/messages")
                    .param("channelId", publicChannel.getId().toString())
                    .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].content").value("Updated message"));

            // 5. 메시지 삭제 (작성자 본인만 가능)
            mockMvc.perform(delete("/api/messages/{messageId}", messageId)
                    .with(csrf())
                    .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNoContent());

            // 6. 삭제 후 조회 시 빈 결과
            mockMvc.perform(get("/api/messages")
                    .param("channelId", publicChannel.getId().toString())
                    .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
        }
    }

    @Nested
    @DisplayName("메시지 생성")
    class CreateMessage {

        @Test
        @WithMockUser
        @DisplayName("텍스트 메시지 생성 성공")
        void createTextMessage_success() throws Exception {
            MessageCreateRequest request = new MessageCreateRequest(
                "Test message content",
                publicChannel.getId(),
                testUser.getId()
            );
            MockMultipartFile requestPart = new MockMultipartFile(
                "messageCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
            );

            mockMvc.perform(multipart("/api/messages")
                    .file(requestPart)
                    .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Test message content"))
                .andExpect(jsonPath("$.channelId").value(publicChannel.getId().toString()))
                .andExpect(jsonPath("$.author.username").value("testuser"));
        }

        @Test
        @WithMockUser
        @DisplayName("첨부파일과 함께 메시지 생성 성공")
        void createMessageWithAttachment_success() throws Exception {
            MessageCreateRequest request = new MessageCreateRequest(
                "Message with attachment",
                publicChannel.getId(),
                testUser.getId()
            );
            MockMultipartFile requestPart = new MockMultipartFile(
                "messageCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
            );
            MockMultipartFile attachment = new MockMultipartFile(
                "attachments",
                "test-file.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "File content".getBytes()
            );

            mockMvc.perform(multipart("/api/messages")
                    .file(requestPart)
                    .file(attachment)
                    .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Message with attachment"))
                .andExpect(jsonPath("$.attachments").isArray())
                .andExpect(jsonPath("$.attachments[0].fileName").value("test-file.txt"));
        }

        @Test
        @WithMockUser
        @DisplayName("빈 내용과 첨부파일 없이 메시지 생성 시 실패")
        void createEmptyMessage_fails() throws Exception {
            MessageCreateRequest request = new MessageCreateRequest(
                "",
                publicChannel.getId(),
                testUser.getId()
            );
            MockMultipartFile requestPart = new MockMultipartFile(
                "messageCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
            );

            mockMvc.perform(multipart("/api/messages")
                    .file(requestPart)
                    .with(csrf()))
                .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser
        @DisplayName("존재하지 않는 채널에 메시지 생성 시 404 반환")
        void createMessage_nonExistentChannel_returns404() throws Exception {
            MessageCreateRequest request = new MessageCreateRequest(
                "Test message",
                UUID.randomUUID(),
                testUser.getId()
            );
            MockMultipartFile requestPart = new MockMultipartFile(
                "messageCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
            );

            mockMvc.perform(multipart("/api/messages")
                    .file(requestPart)
                    .with(csrf()))
                .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser
        @DisplayName("존재하지 않는 작성자로 메시지 생성 시 404 반환")
        void createMessage_nonExistentAuthor_returns404() throws Exception {
            MessageCreateRequest request = new MessageCreateRequest(
                "Test message",
                publicChannel.getId(),
                UUID.randomUUID()
            );
            MockMultipartFile requestPart = new MockMultipartFile(
                "messageCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
            );

            mockMvc.perform(multipart("/api/messages")
                    .file(requestPart)
                    .with(csrf()))
                .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("메시지 조회")
    class FindMessages {

        @Test
        @WithMockUser
        @DisplayName("채널의 메시지 목록 조회 성공")
        void findMessagesByChannel_success() throws Exception {
            // Given: 여러 메시지 생성
            for (int i = 1; i <= 3; i++) {
                MessageCreateRequest request = new MessageCreateRequest(
                    "Message " + i,
                    publicChannel.getId(),
                    testUser.getId()
                );
                MockMultipartFile requestPart = new MockMultipartFile(
                    "messageCreateRequest",
                    "",
                    MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(request)
                );
                mockMvc.perform(multipart("/api/messages")
                        .file(requestPart)
                        .with(csrf()))
                    .andExpect(status().isCreated());
            }

            // When & Then
            mockMvc.perform(get("/api/messages")
                    .param("channelId", publicChannel.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(3));
        }

        @Test
        @WithMockUser
        @DisplayName("빈 채널의 메시지 조회 시 빈 결과 반환")
        void findMessages_emptyChannel_returnsEmpty() throws Exception {
            mockMvc.perform(get("/api/messages")
                    .param("channelId", publicChannel.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
        }

        @Test
        @WithMockUser
        @DisplayName("페이지네이션으로 메시지 조회 성공")
        void findMessages_withPagination_success() throws Exception {
            // Given: 5개 메시지 생성
            for (int i = 1; i <= 5; i++) {
                MessageCreateRequest request = new MessageCreateRequest(
                    "Message " + i,
                    publicChannel.getId(),
                    testUser.getId()
                );
                MockMultipartFile requestPart = new MockMultipartFile(
                    "messageCreateRequest",
                    "",
                    MediaType.APPLICATION_JSON_VALUE,
                    objectMapper.writeValueAsBytes(request)
                );
                mockMvc.perform(multipart("/api/messages")
                        .file(requestPart)
                        .with(csrf()))
                    .andExpect(status().isCreated());
            }

            // When & Then: 첫 페이지 (3개)
            mockMvc.perform(get("/api/messages")
                    .param("channelId", publicChannel.getId().toString())
                    .param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.hasNext").value(true));
        }
    }

    @Nested
    @DisplayName("메시지 수정")
    class UpdateMessage {

        @Test
        @DisplayName("메시지 작성자가 내용 수정 성공")
        void updateMessage_byAuthor_success() throws Exception {
            String accessToken = loginAndGetAccessToken("testuser");

            // Given: 메시지 생성
            MessageCreateRequest createRequest = new MessageCreateRequest(
                "Original content",
                publicChannel.getId(),
                testUser.getId()
            );
            MockMultipartFile requestPart = new MockMultipartFile(
                "messageCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(createRequest)
            );
            MvcResult createResult = mockMvc.perform(multipart("/api/messages")
                    .file(requestPart)
                    .with(csrf())
                    .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isCreated())
                .andReturn();

            String messageId = objectMapper.readTree(
                createResult.getResponse().getContentAsString()
            ).get("id").asText();

            // When & Then
            MessageUpdateRequest updateRequest = new MessageUpdateRequest("Updated content");

            mockMvc.perform(patch("/api/messages/{messageId}", messageId)
                    .with(csrf())
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated content"));
        }

        @Test
        @DisplayName("다른 사용자가 메시지 수정 시 403 반환")
        void updateMessage_byOtherUser_returns403() throws Exception {
            String testUserToken = loginAndGetAccessToken("testuser");
            String otherUserToken = loginAndGetAccessToken("otheruser");

            // Given: testUser가 메시지 생성
            MessageCreateRequest createRequest = new MessageCreateRequest(
                "Original content",
                publicChannel.getId(),
                testUser.getId()
            );
            MockMultipartFile requestPart = new MockMultipartFile(
                "messageCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(createRequest)
            );
            MvcResult createResult = mockMvc.perform(multipart("/api/messages")
                    .file(requestPart)
                    .with(csrf())
                    .header("Authorization", "Bearer " + testUserToken))
                .andExpect(status().isCreated())
                .andReturn();

            String messageId = objectMapper.readTree(
                createResult.getResponse().getContentAsString()
            ).get("id").asText();

            // When & Then: otherUser가 수정 시도
            MessageUpdateRequest updateRequest = new MessageUpdateRequest("Hacked content");

            mockMvc.perform(patch("/api/messages/{messageId}", messageId)
                    .with(csrf())
                    .header("Authorization", "Bearer " + otherUserToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("존재하지 않는 메시지 수정 시 403 반환 (보안상 리소스 존재 여부 숨김)")
        void updateMessage_nonExistent_returns403() throws Exception {
            String accessToken = loginAndGetAccessToken("testuser");

            MessageUpdateRequest updateRequest = new MessageUpdateRequest("Updated content");

            // @PreAuthorize check fails before method execution
            // because isAuthor returns false for non-existent message
            mockMvc.perform(patch("/api/messages/{messageId}", UUID.randomUUID())
                    .with(csrf())
                    .header("Authorization", "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("메시지 삭제")
    class DeleteMessage {

        @Test
        @DisplayName("메시지 작성자가 삭제 성공")
        void deleteMessage_byAuthor_success() throws Exception {
            String accessToken = loginAndGetAccessToken("testuser");

            // Given: 메시지 생성
            MessageCreateRequest createRequest = new MessageCreateRequest(
                "To be deleted",
                publicChannel.getId(),
                testUser.getId()
            );
            MockMultipartFile requestPart = new MockMultipartFile(
                "messageCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(createRequest)
            );
            MvcResult createResult = mockMvc.perform(multipart("/api/messages")
                    .file(requestPart)
                    .with(csrf())
                    .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isCreated())
                .andReturn();

            String messageId = objectMapper.readTree(
                createResult.getResponse().getContentAsString()
            ).get("id").asText();

            // When & Then
            mockMvc.perform(delete("/api/messages/{messageId}", messageId)
                    .with(csrf())
                    .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNoContent());

            // 삭제 확인
            assertThat(messageRepository.findById(UUID.fromString(messageId))).isEmpty();
        }

        @Test
        @DisplayName("다른 사용자가 메시지 삭제 시 403 반환")
        void deleteMessage_byOtherUser_returns403() throws Exception {
            String testUserToken = loginAndGetAccessToken("testuser");
            String otherUserToken = loginAndGetAccessToken("otheruser");

            // Given: testUser가 메시지 생성
            MessageCreateRequest createRequest = new MessageCreateRequest(
                "To be deleted",
                publicChannel.getId(),
                testUser.getId()
            );
            MockMultipartFile requestPart = new MockMultipartFile(
                "messageCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(createRequest)
            );
            MvcResult createResult = mockMvc.perform(multipart("/api/messages")
                    .file(requestPart)
                    .with(csrf())
                    .header("Authorization", "Bearer " + testUserToken))
                .andExpect(status().isCreated())
                .andReturn();

            String messageId = objectMapper.readTree(
                createResult.getResponse().getContentAsString()
            ).get("id").asText();

            // When & Then: otherUser가 삭제 시도
            mockMvc.perform(delete("/api/messages/{messageId}", messageId)
                    .with(csrf())
                    .header("Authorization", "Bearer " + otherUserToken))
                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("존재하지 않는 메시지 삭제 시 403 반환 (보안상 리소스 존재 여부 숨김)")
        void deleteMessage_nonExistent_returns403() throws Exception {
            String accessToken = loginAndGetAccessToken("testuser");

            // @PreAuthorize check fails before method execution
            // because isAuthor returns false for non-existent message
            mockMvc.perform(delete("/api/messages/{messageId}", UUID.randomUUID())
                    .with(csrf())
                    .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("다중 사용자 메시지")
    class MultiUserMessages {

        @Test
        @WithMockUser
        @DisplayName("여러 사용자가 같은 채널에 메시지 작성")
        void multipleUsersPostMessages_success() throws Exception {
            // User1 메시지
            MessageCreateRequest request1 = new MessageCreateRequest(
                "Message from user 1",
                publicChannel.getId(),
                testUser.getId()
            );
            MockMultipartFile requestPart1 = new MockMultipartFile(
                "messageCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request1)
            );
            mockMvc.perform(multipart("/api/messages")
                    .file(requestPart1)
                    .with(csrf()))
                .andExpect(status().isCreated());

            // User2 메시지
            MessageCreateRequest request2 = new MessageCreateRequest(
                "Message from user 2",
                publicChannel.getId(),
                otherUser.getId()
            );
            MockMultipartFile requestPart2 = new MockMultipartFile(
                "messageCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request2)
            );
            mockMvc.perform(multipart("/api/messages")
                    .file(requestPart2)
                    .with(csrf()))
                .andExpect(status().isCreated());

            // 채널 메시지 조회
            mockMvc.perform(get("/api/messages")
                    .param("channelId", publicChannel.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));
        }
    }
}
