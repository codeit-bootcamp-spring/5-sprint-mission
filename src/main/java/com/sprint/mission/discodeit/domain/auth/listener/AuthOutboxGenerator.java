package com.sprint.mission.discodeit.domain.auth.listener;

import com.sprint.mission.discodeit.common.outbox.AggregateType;
import com.sprint.mission.discodeit.common.outbox.OutboxEventWriter;
import com.sprint.mission.discodeit.domain.auth.event.LoginSuccessEvent;
import com.sprint.mission.discodeit.domain.auth.event.RoleUpdatedEvent;
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
public class AuthOutboxGenerator {

    private final OutboxEventWriter outboxEventWriter;

    @Async
    @EventListener
    public void on(LoginSuccessEvent event) {
        outboxEventWriter.write(
            AggregateType.USER,
            event.userId(),
            LoginSuccessEvent.TOPIC,
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
}
