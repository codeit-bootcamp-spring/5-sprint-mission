package com.sprint.mission.discodeit.domain.auth.domain.event;

import com.sprint.mission.discodeit.domain.common.outbox.AggregateType;
import com.sprint.mission.discodeit.domain.common.outbox.OutboxEventWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
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
