package com.sprint.mission.discodeit.event.notification;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.auth.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.message.MessageCreatedEvent;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
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
class NotificationRequiredEventListenerTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ReadStatusRepository readStatusRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationRequiredEventListener listener;

    @Test
    @DisplayName("메시지 생성 시 알림 활성화된 사용자들에게 알림 생성")
    void onMessageCreatedEvent_Success() {
        // given
        UUID messageId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();

        User author = mock(User.class);
        given(author.getId()).willReturn(authorId);
        given(author.getUsername()).willReturn("author");

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

        MessageCreatedEvent event = new MessageCreatedEvent(messageId);

        // when
        listener.on(event);

        // then
        then(notificationService).should().create(
            eq(receiverId),
            eq("author (#general)"),
            eq("Hello, World!")
        );
    }

    @Test
    @DisplayName("비공개 채널 메시지 생성 시 채널명을 DM으로 표시")
    void onMessageCreatedEvent_PrivateChannel_ShowsDM() {
        // given
        UUID messageId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();

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

        MessageCreatedEvent event = new MessageCreatedEvent(messageId);

        // when
        listener.on(event);

        // then
        then(notificationService).should().create(
            eq(receiverId),
            eq("sender (#DM)"),
            eq("Private message")
        );
    }

    @Test
    @DisplayName("여러 사용자에게 알림 생성")
    void onMessageCreatedEvent_MultipleReceivers() {
        // given
        UUID messageId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID receiver1Id = UUID.randomUUID();
        UUID receiver2Id = UUID.randomUUID();

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

        MessageCreatedEvent event = new MessageCreatedEvent(messageId);

        // when
        listener.on(event);

        // then
        then(notificationService).should(times(2)).create(
            any(UUID.class),
            eq("author (#announcements)"),
            eq("Important announcement")
        );
    }

    @Test
    @DisplayName("알림 대상자가 없으면 알림 생성하지 않음")
    void onMessageCreatedEvent_NoReceivers() {
        // given
        UUID messageId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        User author = mock(User.class);
        given(author.getId()).willReturn(authorId);

        Channel channel = mock(Channel.class);
        given(channel.getId()).willReturn(channelId);

        Message message = mock(Message.class);
        given(message.getChannel()).willReturn(channel);
        given(message.getAuthor()).willReturn(author);

        given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
        given(readStatusRepository.findAllByChannelIdWithNotificationEnabled(channelId, authorId))
            .willReturn(List.of());

        MessageCreatedEvent event = new MessageCreatedEvent(messageId);

        // when
        listener.on(event);

        // then
        then(notificationService).should(never()).create(any(), any(), any());
    }

    @Test
    @DisplayName("메시지가 존재하지 않으면 알림 생성하지 않음")
    void onMessageCreatedEvent_MessageNotFound() {
        // given
        UUID messageId = UUID.randomUUID();
        given(messageRepository.findById(messageId)).willReturn(Optional.empty());

        MessageCreatedEvent event = new MessageCreatedEvent(messageId);

        // when
        listener.on(event);

        // then
        then(readStatusRepository).should(never())
            .findAllByChannelIdWithNotificationEnabled(any(), any());
        then(notificationService).should(never()).create(any(), any(), any());
    }

    @Test
    @DisplayName("권한 변경 시 당사자에게 알림 생성")
    void onRoleUpdatedEvent_Success() {
        // given
        UUID userId = UUID.randomUUID();
        Role oldRole = Role.USER;
        Role newRole = Role.CHANNEL_MANAGER;

        RoleUpdatedEvent event = new RoleUpdatedEvent(userId, oldRole, newRole);

        // when
        listener.on(event);

        // then
        then(notificationService).should().create(
            eq(userId),
            eq("권한이 변경되었습니다."),
            eq("USER -> CHANNEL_MANAGER")
        );
    }

    @Test
    @DisplayName("ADMIN으로 권한 변경 시 알림 생성")
    void onRoleUpdatedEvent_ToAdmin() {
        // given
        UUID userId = UUID.randomUUID();
        Role oldRole = Role.CHANNEL_MANAGER;
        Role newRole = Role.ADMIN;

        RoleUpdatedEvent event = new RoleUpdatedEvent(userId, oldRole, newRole);

        // when
        listener.on(event);

        // then
        then(notificationService).should().create(
            eq(userId),
            eq("권한이 변경되었습니다."),
            eq("CHANNEL_MANAGER -> ADMIN")
        );
    }

    @Test
    @DisplayName("권한 강등 시 알림 생성")
    void onRoleUpdatedEvent_Demotion() {
        // given
        UUID userId = UUID.randomUUID();
        Role oldRole = Role.ADMIN;
        Role newRole = Role.USER;

        RoleUpdatedEvent event = new RoleUpdatedEvent(userId, oldRole, newRole);

        // when
        listener.on(event);

        // then
        then(notificationService).should().create(
            eq(userId),
            eq("권한이 변경되었습니다."),
            eq("ADMIN -> USER")
        );
    }
}
