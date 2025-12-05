package com.sprint.mission.discodeit.domain.user.listener;

import com.sprint.mission.discodeit.domain.outbox.OutboxEventWriter;
import com.sprint.mission.discodeit.domain.user.event.UserDeletedEvent;
import com.sprint.mission.discodeit.infrastructrue.outbox.entity.AggregateType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserOutboxGenerator {

    private final OutboxEventWriter outboxEventWriter;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void on(UserDeletedEvent event) {
        outboxEventWriter.write(
            AggregateType.USER,
            event.userId(),
            UserDeletedEvent.TOPIC,
            event
        );
    }
}
