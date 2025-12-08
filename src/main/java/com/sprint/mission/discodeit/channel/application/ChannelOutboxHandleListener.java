package com.sprint.mission.discodeit.channel.application;

import com.sprint.mission.discodeit.channel.domain.dto.ChannelDeletedEvent;
import com.sprint.mission.discodeit.common.infrastructure.outbox.AggregateType;
import com.sprint.mission.discodeit.common.infrastructure.outbox.OutboxEventWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChannelOutboxHandleListener {

    private final OutboxEventWriter outboxEventWriter;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void on(ChannelDeletedEvent event) {
        log.debug("Channel deleted event received: {}", event);

        outboxEventWriter.write(
            AggregateType.CHANNEL,
            event.channelId(),
            ChannelDeletedEvent.TOPIC,
            event
        );
    }
}
