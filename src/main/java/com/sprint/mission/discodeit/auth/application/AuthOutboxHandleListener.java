package com.sprint.mission.discodeit.auth.application;

import com.sprint.mission.discodeit.auth.domain.CredentialUpdated;
import com.sprint.mission.discodeit.auth.domain.LoginEvent;
import com.sprint.mission.discodeit.auth.domain.LogoutEvent;
import com.sprint.mission.discodeit.auth.domain.RoleUpdatedEvent;
import com.sprint.mission.discodeit.auth.domain.TokenRefreshEvent;
import com.sprint.mission.discodeit.common.infrastructure.outbox.AggregateType;
import com.sprint.mission.discodeit.common.infrastructure.outbox.OutboxEventWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class AuthOutboxHandleListener {

    private final OutboxEventWriter outboxEventWriter;

    @Async
    @EventListener
    public void on(LoginEvent event) {
        outboxEventWriter.write(
            AggregateType.USER,
            event.userId(),
            LoginEvent.TOPIC,
            event
        );
    }

    @Async
    @EventListener
    public void on(LogoutEvent event) {
        outboxEventWriter.write(
            AggregateType.USER,
            event.userId(),
            LogoutEvent.TOPIC,
            event
        );
    }

    @Async
    @EventListener
    public void on(TokenRefreshEvent event) {
        outboxEventWriter.write(
            AggregateType.USER,
            event.userId(),
            TokenRefreshEvent.TOPIC,
            event
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void on(RoleUpdatedEvent event) {
        outboxEventWriter.write(
            AggregateType.USER,
            event.userId(),
            RoleUpdatedEvent.TOPIC,
            event
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void on(CredentialUpdated event) {
        outboxEventWriter.write(
            AggregateType.USER,
            event.userId(),
            CredentialUpdated.TOPIC,
            event
        );
    }
}
