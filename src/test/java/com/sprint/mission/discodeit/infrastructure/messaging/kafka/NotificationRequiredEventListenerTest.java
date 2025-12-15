package com.sprint.mission.discodeit.infrastructure.messaging.kafka;

import com.sprint.mission.discodeit.auth.domain.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.channel.domain.Channel;
import com.sprint.mission.discodeit.channel.domain.ChannelType;
import com.sprint.mission.discodeit.message.domain.Message;
import com.sprint.mission.discodeit.message.domain.MessageRepository;
import com.sprint.mission.discodeit.message.domain.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.notification.application.NotificationService;
import com.sprint.mission.discodeit.readstatus.domain.ReadStatus;
import com.sprint.mission.discodeit.readstatus.domain.ReadStatusRepository;
import com.sprint.mission.discodeit.user.domain.Role;
import com.sprint.mission.discodeit.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationRequiredEventListener 단위 테스트")
class NotificationRequiredEventListenerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ReadStatusRepository readStatusRepository;

    @InjectMocks
    private NotificationRequiredEventListener listener;

    @Nested
    @DisplayName("onMessageCreated")
    class OnMessageCreated {

        @Test
        @DisplayName("메시지 생성 이벤트 수신 시 알림 대상에게 알림 생성")
        void onMessageCreated_createsNotificationsForTargets() {
            // given
            UUID messageId = UUID.randomUUID();
            UUID authorId = UUID.randomUUID();
            UUID channelId = UUID.randomUUID();
            UUID targetUserId = UUID.randomUUID();

            User author = createUser(authorId, "author", "author@example.com");
            User targetUser = createUser(targetUserId, "target", "target@example.com");
            Channel channel = createChannel(channelId, ChannelType.PUBLIC, "general");
            Message message = createMessage(messageId, "Hello!", channel, author);
            ReadStatus readStatus = createReadStatus(targetUser, channel);

            MessageCreatedEvent event = new MessageCreatedEvent(messageId);

            given(messageRepository.findWithAuthorAndChannelById(messageId)).willReturn(Optional.of(message));
            given(readStatusRepository.findNotificationTargets(channelId, authorId))
                .willReturn(List.of(readStatus));

            // when
            listener.onMessageCreated(event);

            // then
            then(notificationService).should().create(
                targetUserId,
                "author (#general)",
                "Hello!"
            );
        }

        @Test
        @DisplayName("DM 채널에서 메시지 생성 시 채널명 대신 DM 표시")
        void onMessageCreated_dmChannel_showsDmInTitle() {
            // given
            UUID messageId = UUID.randomUUID();
            UUID authorId = UUID.randomUUID();
            UUID channelId = UUID.randomUUID();
            UUID targetUserId = UUID.randomUUID();

            User author = createUser(authorId, "sender", "sender@example.com");
            User targetUser = createUser(targetUserId, "receiver", "receiver@example.com");
            Channel dmChannel = createChannel(channelId, ChannelType.PRIVATE, null);
            Message message = createMessage(messageId, "Private message", dmChannel, author);
            ReadStatus readStatus = createReadStatus(targetUser, dmChannel);

            MessageCreatedEvent event = new MessageCreatedEvent(messageId);

            given(messageRepository.findWithAuthorAndChannelById(messageId)).willReturn(Optional.of(message));
            given(readStatusRepository.findNotificationTargets(channelId, authorId))
                .willReturn(List.of(readStatus));

            // when
            listener.onMessageCreated(event);

            // then
            then(notificationService).should().create(
                targetUserId,
                "sender (#DM)",
                "Private message"
            );
        }

        @Test
        @DisplayName("메시지를 찾을 수 없는 경우 알림 생성하지 않음")
        void onMessageCreated_messageNotFound_doesNotCreateNotification() {
            // given
            UUID messageId = UUID.randomUUID();
            MessageCreatedEvent event = new MessageCreatedEvent(messageId);

            given(messageRepository.findWithAuthorAndChannelById(messageId)).willReturn(Optional.empty());

            // when
            listener.onMessageCreated(event);

            // then
            then(notificationService).should(never()).create(
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any()
            );
        }

        @Test
        @DisplayName("알림 대상이 없는 경우 알림 생성하지 않음")
        void onMessageCreated_noTargets_doesNotCreateNotification() {
            // given
            UUID messageId = UUID.randomUUID();
            UUID authorId = UUID.randomUUID();
            UUID channelId = UUID.randomUUID();

            User author = createUser(authorId, "author", "author@example.com");
            Channel channel = createChannel(channelId, ChannelType.PUBLIC, "general");
            Message message = createMessage(messageId, "Hello!", channel, author);

            MessageCreatedEvent event = new MessageCreatedEvent(messageId);

            given(messageRepository.findWithAuthorAndChannelById(messageId)).willReturn(Optional.of(message));
            given(readStatusRepository.findNotificationTargets(channelId, authorId))
                .willReturn(Collections.emptyList());

            // when
            listener.onMessageCreated(event);

            // then
            then(notificationService).should(never()).create(
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any()
            );
        }

        @Test
        @DisplayName("다수의 알림 대상에게 각각 알림 생성")
        void onMessageCreated_multipleTargets_createsNotificationsForEach() {
            // given
            UUID messageId = UUID.randomUUID();
            UUID authorId = UUID.randomUUID();
            UUID channelId = UUID.randomUUID();
            UUID targetUserId1 = UUID.randomUUID();
            UUID targetUserId2 = UUID.randomUUID();

            User author = createUser(authorId, "author", "author@example.com");
            User targetUser1 = createUser(targetUserId1, "target1", "target1@example.com");
            User targetUser2 = createUser(targetUserId2, "target2", "target2@example.com");
            Channel channel = createChannel(channelId, ChannelType.PUBLIC, "general");
            Message message = createMessage(messageId, "Hello everyone!", channel, author);
            ReadStatus readStatus1 = createReadStatus(targetUser1, channel);
            ReadStatus readStatus2 = createReadStatus(targetUser2, channel);

            MessageCreatedEvent event = new MessageCreatedEvent(messageId);

            given(messageRepository.findWithAuthorAndChannelById(messageId)).willReturn(Optional.of(message));
            given(readStatusRepository.findNotificationTargets(channelId, authorId))
                .willReturn(List.of(readStatus1, readStatus2));

            // when
            listener.onMessageCreated(event);

            // then
            then(notificationService).should().create(
                targetUserId1,
                "author (#general)",
                "Hello everyone!"
            );
            then(notificationService).should().create(
                targetUserId2,
                "author (#general)",
                "Hello everyone!"
            );
        }
    }

    @Nested
    @DisplayName("onRoleUpdated")
    class OnRoleUpdated {

        @Test
        @DisplayName("권한 변경 이벤트 수신 시 알림 생성")
        void onRoleUpdated_createsNotification() {
            // given
            UUID userId = UUID.randomUUID();
            RoleUpdatedEvent event = new RoleUpdatedEvent(
                userId, "testuser", Role.USER, Role.CHANNEL_MANAGER
            );

            // when
            listener.onRoleUpdated(event);

            // then
            then(notificationService).should().create(
                userId,
                "권한이 변경되었습니다.",
                "USER -> CHANNEL_MANAGER"
            );
        }

        @Test
        @DisplayName("ADMIN으로 권한 승격 시 알림 생성")
        void onRoleUpdated_promoteToAdmin_createsNotification() {
            // given
            UUID userId = UUID.randomUUID();
            RoleUpdatedEvent event = new RoleUpdatedEvent(
                userId, "adminuser", Role.CHANNEL_MANAGER, Role.ADMIN
            );

            // when
            listener.onRoleUpdated(event);

            // then
            then(notificationService).should().create(
                userId,
                "권한이 변경되었습니다.",
                "CHANNEL_MANAGER -> ADMIN"
            );
        }

        @Test
        @DisplayName("권한 강등 시 알림 생성")
        void onRoleUpdated_demoteRole_createsNotification() {
            // given
            UUID userId = UUID.randomUUID();
            RoleUpdatedEvent event = new RoleUpdatedEvent(
                userId, "demoteduser", Role.ADMIN, Role.USER
            );

            // when
            listener.onRoleUpdated(event);

            // then
            then(notificationService).should().create(
                userId,
                "권한이 변경되었습니다.",
                "ADMIN -> USER"
            );
        }
    }

    private User createUser(UUID id, String username, String email) {
        User user = new User(username, email, "encodedPassword123456789012345678901234567890123456789012", null);
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    private Channel createChannel(UUID id, ChannelType type, String name) {
        Channel channel = new Channel(type, name, null);
        ReflectionTestUtils.setField(channel, "id", id);
        return channel;
    }

    private Message createMessage(UUID id, String content, Channel channel, User author) {
        Message message = new Message(content, channel, author);
        ReflectionTestUtils.setField(message, "id", id);
        return message;
    }

    private ReadStatus createReadStatus(User user, Channel channel) {
        return new ReadStatus(user, channel, Instant.now(), true);
    }
}
