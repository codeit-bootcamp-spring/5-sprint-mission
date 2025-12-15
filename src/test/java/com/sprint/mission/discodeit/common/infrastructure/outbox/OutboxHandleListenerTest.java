package com.sprint.mission.discodeit.common.infrastructure.outbox;

import com.sprint.mission.discodeit.auth.domain.event.CredentialUpdatedEvent;
import com.sprint.mission.discodeit.auth.domain.event.LoginEvent;
import com.sprint.mission.discodeit.auth.domain.event.LogoutEvent;
import com.sprint.mission.discodeit.auth.domain.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.auth.domain.event.TokenRefreshEvent;
import com.sprint.mission.discodeit.channel.domain.ChannelType;
import com.sprint.mission.discodeit.channel.domain.event.ChannelDeletedEvent;
import com.sprint.mission.discodeit.message.domain.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.message.domain.event.MessageDeletedEvent;
import com.sprint.mission.discodeit.user.domain.Role;
import com.sprint.mission.discodeit.user.domain.event.UserDeletedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("OutboxHandleListener 단위 테스트")
class OutboxHandleListenerTest {

    @Mock
    private OutboxEventWriter outboxEventWriter;

    @InjectMocks
    private OutboxHandleListener listener;

    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USERNAME = "testuser";
    private static final String IP_ADDRESS = "192.168.1.1";
    private static final String USER_AGENT = "Mozilla/5.0";

    @Nested
    @DisplayName("Auth Events")
    class AuthEvents {

        @Test
        @DisplayName("LoginEvent 수신 시 OutboxEventWriter에 이벤트 저장")
        void on_withLoginEvent_writesToOutbox() {
            // given
            LoginEvent event = new LoginEvent(USER_ID, USERNAME, IP_ADDRESS, USER_AGENT, 100L);

            // when
            listener.on(event);

            // then
            then(outboxEventWriter).should().write(
                AggregateType.USER,
                USER_ID,
                LoginEvent.TOPIC,
                event
            );
        }

        @Test
        @DisplayName("LogoutEvent 수신 시 OutboxEventWriter에 이벤트 저장")
        void on_withLogoutEvent_writesToOutbox() {
            // given
            LogoutEvent event = new LogoutEvent(USER_ID, USERNAME, IP_ADDRESS, USER_AGENT);

            // when
            listener.on(event);

            // then
            then(outboxEventWriter).should().write(
                AggregateType.USER,
                USER_ID,
                LogoutEvent.TOPIC,
                event
            );
        }

        @Test
        @DisplayName("TokenRefreshEvent 수신 시 OutboxEventWriter에 이벤트 저장")
        void on_withTokenRefreshEvent_writesToOutbox() {
            // given
            TokenRefreshEvent event = new TokenRefreshEvent(USER_ID, USERNAME, IP_ADDRESS, USER_AGENT);

            // when
            listener.on(event);

            // then
            then(outboxEventWriter).should().write(
                AggregateType.USER,
                USER_ID,
                TokenRefreshEvent.TOPIC,
                event
            );
        }

        @Test
        @DisplayName("RoleUpdatedEvent 수신 시 OutboxEventWriter에 이벤트 저장")
        void on_withRoleUpdatedEvent_writesToOutbox() {
            // given
            RoleUpdatedEvent event = new RoleUpdatedEvent(USER_ID, USERNAME, Role.USER, Role.ADMIN);

            // when
            listener.on(event);

            // then
            then(outboxEventWriter).should().write(
                AggregateType.USER,
                USER_ID,
                RoleUpdatedEvent.TOPIC,
                event
            );
        }

        @Test
        @DisplayName("CredentialUpdatedEvent 수신 시 OutboxEventWriter에 이벤트 저장")
        void on_withCredentialUpdatedEvent_writesToOutbox() {
            // given
            CredentialUpdatedEvent event = new CredentialUpdatedEvent(USER_ID, USERNAME, IP_ADDRESS, USER_AGENT);

            // when
            listener.on(event);

            // then
            then(outboxEventWriter).should().write(
                AggregateType.USER,
                USER_ID,
                CredentialUpdatedEvent.TOPIC,
                event
            );
        }
    }

    @Nested
    @DisplayName("User Events")
    class UserEvents {

        @Test
        @DisplayName("UserDeletedEvent 수신 시 OutboxEventWriter에 이벤트 저장")
        void on_withUserDeletedEvent_writesToOutbox() {
            // given
            UserDeletedEvent event = new UserDeletedEvent(USER_ID);

            // when
            listener.on(event);

            // then
            then(outboxEventWriter).should().write(
                AggregateType.USER,
                USER_ID,
                UserDeletedEvent.TOPIC,
                event
            );
        }
    }

    @Nested
    @DisplayName("Channel Events")
    class ChannelEvents {

        @Test
        @DisplayName("ChannelDeletedEvent 수신 시 OutboxEventWriter에 이벤트 저장")
        void on_withChannelDeletedEvent_writesToOutbox() {
            // given
            UUID channelId = UUID.randomUUID();
            ChannelDeletedEvent event = new ChannelDeletedEvent(channelId, ChannelType.PUBLIC);

            // when
            listener.on(event);

            // then
            then(outboxEventWriter).should().write(
                AggregateType.CHANNEL,
                channelId,
                ChannelDeletedEvent.TOPIC,
                event
            );
        }

        @Test
        @DisplayName("PRIVATE 채널 삭제 이벤트도 동일하게 처리")
        void on_withPrivateChannelDeletedEvent_writesToOutbox() {
            // given
            UUID channelId = UUID.randomUUID();
            ChannelDeletedEvent event = new ChannelDeletedEvent(channelId, ChannelType.PRIVATE);

            // when
            listener.on(event);

            // then
            then(outboxEventWriter).should().write(
                AggregateType.CHANNEL,
                channelId,
                ChannelDeletedEvent.TOPIC,
                event
            );
        }
    }

    @Nested
    @DisplayName("Message Events")
    class MessageEvents {

        @Test
        @DisplayName("MessageCreatedEvent 수신 시 OutboxEventWriter에 이벤트 저장")
        void on_withMessageCreatedEvent_writesToOutbox() {
            // given
            UUID messageId = UUID.randomUUID();
            MessageCreatedEvent event = new MessageCreatedEvent(messageId);

            // when
            listener.on(event);

            // then
            then(outboxEventWriter).should().write(
                AggregateType.MESSAGE,
                messageId,
                MessageCreatedEvent.TOPIC,
                event
            );
        }

        @Test
        @DisplayName("MessageDeletedEvent 수신 시 OutboxEventWriter에 이벤트 저장")
        void on_withMessageDeletedEvent_writesToOutbox() {
            // given
            UUID messageId = UUID.randomUUID();
            MessageDeletedEvent event = new MessageDeletedEvent(messageId);

            // when
            listener.on(event);

            // then
            then(outboxEventWriter).should().write(
                AggregateType.MESSAGE,
                messageId,
                MessageDeletedEvent.TOPIC,
                event
            );
        }
    }
}
