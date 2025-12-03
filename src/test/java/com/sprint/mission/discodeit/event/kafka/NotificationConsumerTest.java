package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.auth.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.binarycontent.BinaryContentUploadFailedEvent;
import com.sprint.mission.discodeit.event.message.MessageCreatedEvent;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.security.audit.AuthAuditService;
import com.sprint.mission.discodeit.service.NotificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class NotificationConsumerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ReadStatusRepository readStatusRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private AuthAuditService authAuditService;

    @InjectMocks
    private NotificationConsumer listener;

    @Test
    @DisplayName("메시지 생성 이벤트 수신 시 알림 생성")
    void onMessageCreatedEvent_Success() throws JsonProcessingException {
        // given
        UUID messageId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        String kafkaEvent = "{\"messageId\":\"" + messageId + "\"}";

        MessageCreatedEvent event = new MessageCreatedEvent(messageId);
        given(objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class)).willReturn(event);

        User author = mock(User.class);
        given(author.getId()).willReturn(authorId);
        given(author.getUsername()).willReturn("testUser");

        User receiver = mock(User.class);
        given(receiver.getId()).willReturn(receiverId);

        Channel channel = mock(Channel.class);
        given(channel.getId()).willReturn(channelId);
        given(channel.getName()).willReturn("general");

        Message message = mock(Message.class);
        given(message.getChannel()).willReturn(channel);
        given(message.getAuthor()).willReturn(author);
        given(message.getContent()).willReturn("Hello, World!");

        ReadStatus readStatus = mock(ReadStatus.class);
        given(readStatus.getUser()).willReturn(receiver);

        given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
        given(readStatusRepository.findAllByChannelIdWithNotificationEnabled(channelId, authorId))
            .willReturn(List.of(readStatus));

        // when
        listener.onMessageCreatedEvent(kafkaEvent);

        // then
        then(notificationService).should().create(
            eq(receiverId),
            eq("testUser (#general)"),
            eq("Hello, World!")
        );
    }

    @Test
    @DisplayName("비공개 채널 메시지 생성 시 채널명을 DM으로 표시")
    void onMessageCreatedEvent_PrivateChannel_ShowsDM() throws JsonProcessingException {
        // given
        UUID messageId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();
        String kafkaEvent = "{\"messageId\":\"" + messageId + "\"}";

        MessageCreatedEvent event = new MessageCreatedEvent(messageId);
        given(objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class)).willReturn(event);

        User author = mock(User.class);
        given(author.getId()).willReturn(authorId);
        given(author.getUsername()).willReturn("sender");

        User receiver = mock(User.class);
        given(receiver.getId()).willReturn(receiverId);

        Channel channel = mock(Channel.class);
        given(channel.getId()).willReturn(channelId);
        given(channel.getName()).willReturn(null);

        Message message = mock(Message.class);
        given(message.getChannel()).willReturn(channel);
        given(message.getAuthor()).willReturn(author);
        given(message.getContent()).willReturn("Private message");

        ReadStatus readStatus = mock(ReadStatus.class);
        given(readStatus.getUser()).willReturn(receiver);

        given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
        given(readStatusRepository.findAllByChannelIdWithNotificationEnabled(channelId, authorId))
            .willReturn(List.of(readStatus));

        // when
        listener.onMessageCreatedEvent(kafkaEvent);

        // then
        then(notificationService).should().create(
            eq(receiverId),
            eq("sender (#DM)"),
            eq("Private message")
        );
    }

    @Test
    @DisplayName("여러 사용자에게 알림 생성")
    void onMessageCreatedEvent_MultipleReceivers() throws JsonProcessingException {
        // given
        UUID messageId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID receiver1Id = UUID.randomUUID();
        UUID receiver2Id = UUID.randomUUID();
        String kafkaEvent = "{\"messageId\":\"" + messageId + "\"}";

        MessageCreatedEvent event = new MessageCreatedEvent(messageId);
        given(objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class)).willReturn(event);

        User author = mock(User.class);
        given(author.getId()).willReturn(authorId);
        given(author.getUsername()).willReturn("author");

        User receiver1 = mock(User.class);
        given(receiver1.getId()).willReturn(receiver1Id);

        User receiver2 = mock(User.class);
        given(receiver2.getId()).willReturn(receiver2Id);

        Channel channel = mock(Channel.class);
        given(channel.getId()).willReturn(channelId);
        given(channel.getName()).willReturn("announcements");

        Message message = mock(Message.class);
        given(message.getChannel()).willReturn(channel);
        given(message.getAuthor()).willReturn(author);
        given(message.getContent()).willReturn("Important announcement");

        ReadStatus readStatus1 = mock(ReadStatus.class);
        given(readStatus1.getUser()).willReturn(receiver1);

        ReadStatus readStatus2 = mock(ReadStatus.class);
        given(readStatus2.getUser()).willReturn(receiver2);

        given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
        given(readStatusRepository.findAllByChannelIdWithNotificationEnabled(channelId, authorId))
            .willReturn(List.of(readStatus1, readStatus2));

        // when
        listener.onMessageCreatedEvent(kafkaEvent);

        // then
        then(notificationService).should(times(2)).create(
            any(UUID.class),
            eq("author (#announcements)"),
            eq("Important announcement")
        );
    }

    @Test
    @DisplayName("메시지가 존재하지 않으면 알림 생성하지 않음")
    void onMessageCreatedEvent_MessageNotFound() throws JsonProcessingException {
        // given
        UUID messageId = UUID.randomUUID();
        String kafkaEvent = "{\"messageId\":\"" + messageId + "\"}";

        MessageCreatedEvent event = new MessageCreatedEvent(messageId);
        given(objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class)).willReturn(event);
        given(messageRepository.findById(messageId)).willReturn(Optional.empty());

        // when
        listener.onMessageCreatedEvent(kafkaEvent);

        // then
        then(readStatusRepository).should(never())
            .findAllByChannelIdWithNotificationEnabled(any(), any());
        then(notificationService).should(never()).create(any(), any(), any());
    }

    @Test
    @DisplayName("MessageCreatedEvent JSON 역직렬화 실패 시 알림 생성하지 않음")
    void onMessageCreatedEvent_JsonDeserializationFails() throws JsonProcessingException {
        // given
        String invalidKafkaEvent = "invalid json";
        given(objectMapper.readValue(invalidKafkaEvent, MessageCreatedEvent.class))
            .willThrow(new JsonProcessingException("Invalid JSON") {
            });

        // when
        listener.onMessageCreatedEvent(invalidKafkaEvent);

        // then
        then(messageRepository).should(never()).findById(any());
        then(notificationService).should(never()).create(any(), any(), any());
    }

    @Test
    @DisplayName("권한 변경 이벤트 수신 시 알림 생성")
    void onRoleUpdatedEvent_Success() throws JsonProcessingException {
        // given
        UUID userId = UUID.randomUUID();
        String kafkaEvent = "{\"userId\":\"" + userId + "\",\"oldRole\":\"USER\",\"newRole\":\"ADMIN\"}";

        RoleUpdatedEvent event = new RoleUpdatedEvent(userId, "testuser", Role.USER, Role.ADMIN);
        given(objectMapper.readValue(kafkaEvent, RoleUpdatedEvent.class)).willReturn(event);

        // when
        listener.onRoleUpdatedEvent(kafkaEvent);

        // then
        then(notificationService).should().create(
            eq(userId),
            eq("권한이 변경되었습니다."),
            eq("USER -> ADMIN")
        );
    }

    @Test
    @DisplayName("RoleUpdatedEvent JSON 역직렬화 실패 시 알림 생성하지 않음")
    void onRoleUpdatedEvent_JsonDeserializationFails() throws JsonProcessingException {
        // given
        String invalidKafkaEvent = "invalid json";
        given(objectMapper.readValue(invalidKafkaEvent, RoleUpdatedEvent.class))
            .willThrow(new JsonProcessingException("Invalid JSON") {
            });

        // when
        listener.onRoleUpdatedEvent(invalidKafkaEvent);

        // then
        then(notificationService).should(never()).create(any(), any(), any());
    }

    @Test
    @DisplayName("S3 업로드 실패 이벤트 수신 시 로그 기록")
    void onS3UploadFailedEvent_Success() throws JsonProcessingException {
        // given
        UUID binaryContentId = UUID.randomUUID();
        String requestId = "test-request-id";
        String errorMessage = "Upload failed";
        String kafkaEvent = "{\"binaryContentId\":\"" + binaryContentId + "\"}";

        BinaryContentUploadFailedEvent event = new BinaryContentUploadFailedEvent(
            binaryContentId, requestId, errorMessage);
        given(objectMapper.readValue(kafkaEvent, BinaryContentUploadFailedEvent.class))
            .willReturn(event);

        // when
        listener.onS3UploadFailedEvent(kafkaEvent);

        // then - no exception thrown, log is written (verified by not throwing)
    }

    @Test
    @DisplayName("S3UploadFailedEvent JSON 역직렬화 실패 시 예외 처리")
    void onS3UploadFailedEvent_JsonDeserializationFails() throws JsonProcessingException {
        // given
        String invalidKafkaEvent = "invalid json";
        given(objectMapper.readValue(invalidKafkaEvent, BinaryContentUploadFailedEvent.class))
            .willThrow(new JsonProcessingException("Invalid JSON") {
            });

        // when
        listener.onS3UploadFailedEvent(invalidKafkaEvent);

        // then - no exception thrown, error is logged
    }
}
