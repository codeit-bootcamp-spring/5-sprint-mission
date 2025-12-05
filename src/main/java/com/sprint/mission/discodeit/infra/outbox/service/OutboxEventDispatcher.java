package com.sprint.mission.discodeit.infra.outbox.service;

import com.sprint.mission.discodeit.domain.event.auth.LoginSuccessEvent;
import com.sprint.mission.discodeit.domain.event.auth.RoleUpdatedEvent;
import com.sprint.mission.discodeit.domain.event.channel.ChannelDeletedEvent;
import com.sprint.mission.discodeit.domain.event.message.MessageCreatedEvent;
import com.sprint.mission.discodeit.domain.event.message.MessageDeletedEvent;
import com.sprint.mission.discodeit.domain.event.user.UserDeletedEvent;
import com.sprint.mission.discodeit.infra.outbox.entity.AggregateType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxEventDispatcher {

    private final OutboxEventWriter outboxEventWriter;

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
    public void on(UserDeletedEvent event) {
        outboxEventWriter.write(
            AggregateType.USER,
            event.userId(),
            UserDeletedEvent.TOPIC,
            event
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void on(MessageCreatedEvent event) {
        outboxEventWriter.write(
            AggregateType.MESSAGE,
            event.messageId(),
            MessageCreatedEvent.TOPIC,
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
    public void on(MessageDeletedEvent event) {
        outboxEventWriter.write(
            AggregateType.MESSAGE,
            event.messageId(),
            MessageDeletedEvent.TOPIC,
            event
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void on(ChannelDeletedEvent event) {
        outboxEventWriter.write(
            AggregateType.CHANNEL,
            event.channelId(),
            ChannelDeletedEvent.TOPIC,
            event
        );
    }

    private String resolveTopic(Object event) {
        return "discodeit." + event.getClass().getSimpleName();
    }
}
