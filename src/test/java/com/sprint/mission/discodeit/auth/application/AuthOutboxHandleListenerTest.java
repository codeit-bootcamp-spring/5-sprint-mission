package com.sprint.mission.discodeit.auth.application;

import com.sprint.mission.discodeit.auth.domain.event.CredentialUpdatedEvent;
import com.sprint.mission.discodeit.auth.domain.event.LoginEvent;
import com.sprint.mission.discodeit.auth.domain.event.LogoutEvent;
import com.sprint.mission.discodeit.auth.domain.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.auth.domain.event.TokenRefreshEvent;
import com.sprint.mission.discodeit.common.infrastructure.outbox.AggregateType;
import com.sprint.mission.discodeit.common.infrastructure.outbox.OutboxEventWriter;
import com.sprint.mission.discodeit.user.domain.Role;
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
@DisplayName("AuthOutboxHandleListener 단위 테스트")
class AuthOutboxHandleListenerTest {

    @Mock
    private OutboxEventWriter outboxEventWriter;

    @InjectMocks
    private AuthOutboxHandleListener listener;

    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USERNAME = "testuser";
    private static final String IP_ADDRESS = "192.168.1.1";
    private static final String USER_AGENT = "Mozilla/5.0";

    @Nested
    @DisplayName("on(LoginEvent)")
    class OnLoginEventTest {

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
    }

    @Nested
    @DisplayName("on(LogoutEvent)")
    class OnLogoutEventTest {

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
    }

    @Nested
    @DisplayName("on(TokenRefreshEvent)")
    class OnTokenRefreshEventTest {

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
    }

    @Nested
    @DisplayName("on(RoleUpdatedEvent)")
    class OnRoleUpdatedEventTest {

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
    }

    @Nested
    @DisplayName("on(CredentialUpdatedEvent)")
    class OnCredentialUpdatedEventTest {

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
}
