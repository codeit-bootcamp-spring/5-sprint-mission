package com.sprint.mission.discodeit.domain.auth.domain.event;

import com.sprint.mission.discodeit.domain.common.outbox.AggregateType;
import com.sprint.mission.discodeit.domain.common.outbox.OutboxEventWriter;
import com.sprint.mission.discodeit.domain.user.domain.Role;
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

    private static final UUID TEST_USER_ID = UUID.randomUUID();
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_IP = "127.0.0.1";
    private static final String TEST_USER_AGENT = "Mozilla/5.0";

    @Nested
    @DisplayName("LoginEvent 처리")
    class OnLoginEvent {

        @Test
        @DisplayName("LoginEvent를 Outbox에 저장")
        void on_loginSuccessEvent_writesToOutbox() {
            // given
            LoginEvent event = new LoginEvent(
                TEST_USER_ID, TEST_USERNAME, TEST_IP, TEST_USER_AGENT, 150L
            );

            // when
            listener.on(event);

            // then
            then(outboxEventWriter).should().write(
                AggregateType.USER,
                TEST_USER_ID,
                LoginEvent.TOPIC,
                event
            );
        }
    }

    @Nested
    @DisplayName("RoleUpdatedEvent 처리")
    class OnRoleUpdatedEvent {

        @Test
        @DisplayName("RoleUpdatedEvent를 Outbox에 저장")
        void on_roleUpdatedEvent_writesToOutbox() {
            // given
            RoleUpdatedEvent event = new RoleUpdatedEvent(
                TEST_USER_ID, TEST_USERNAME, Role.USER, Role.CHANNEL_MANAGER
            );

            // when
            listener.on(event);

            // then
            then(outboxEventWriter).should().write(
                AggregateType.USER,
                TEST_USER_ID,
                RoleUpdatedEvent.TOPIC,
                event
            );
        }
    }
}
