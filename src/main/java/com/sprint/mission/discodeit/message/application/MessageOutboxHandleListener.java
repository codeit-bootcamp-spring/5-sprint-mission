package com.sprint.mission.discodeit.message.application;

import com.sprint.mission.discodeit.common.infrastructure.outbox.AggregateType;
import com.sprint.mission.discodeit.common.infrastructure.outbox.OutboxEventWriter;
import com.sprint.mission.discodeit.message.domain.event.MessageCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageOutboxHandleListener {

    private final OutboxEventWriter outboxEventWriter;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void on(MessageCreatedEvent event) {
        log.debug("Message created event received: [event={}]", event);

        outboxEventWriter.write(
            AggregateType.MESSAGE,
            event.messageId(),
            MessageCreatedEvent.TOPIC,
            event
        );
    }
}
