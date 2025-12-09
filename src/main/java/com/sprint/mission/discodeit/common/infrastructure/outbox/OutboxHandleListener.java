package com.sprint.mission.discodeit.common.infrastructure.outbox;

import com.sprint.mission.discodeit.auth.domain.event.CredentialUpdatedEvent;
import com.sprint.mission.discodeit.auth.domain.event.LoginEvent;
import com.sprint.mission.discodeit.auth.domain.event.LogoutEvent;
import com.sprint.mission.discodeit.auth.domain.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.auth.domain.event.TokenRefreshEvent;
import com.sprint.mission.discodeit.channel.domain.event.ChannelDeletedEvent;
import com.sprint.mission.discodeit.message.domain.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.user.domain.event.UserDeletedEvent;
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
public class OutboxHandleListener {

    private final OutboxEventWriter outboxEventWriter;

    // Auth Events

    @Async
    @EventListener
    public void on(LoginEvent event) {
        log.debug("Login event received: [event={}]", event);

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
        log.debug("Logout event received: [event={}]", event);

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
        log.debug("Token refresh event received: [event={}]", event);

        outboxEventWriter.write(
            AggregateType.USER,
            event.userId(),
            TokenRefreshEvent.TOPIC,
            event
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void on(RoleUpdatedEvent event) {
        log.debug("Role updated event received: [event={}]", event);

        outboxEventWriter.write(
            AggregateType.USER,
            event.userId(),
            RoleUpdatedEvent.TOPIC,
            event
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void on(CredentialUpdatedEvent event) {
        log.debug("Credential updated event received: [event={}]", event);

        outboxEventWriter.write(
            AggregateType.USER,
            event.userId(),
            CredentialUpdatedEvent.TOPIC,
            event
        );
    }

    // User Events

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void on(UserDeletedEvent event) {
        log.debug("User deleted event received: [event={}]", event);

        outboxEventWriter.write(
            AggregateType.USER,
            event.userId(),
            UserDeletedEvent.TOPIC,
            event
        );
    }

    // Channel Events

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void on(ChannelDeletedEvent event) {
        log.debug("Channel deleted event received: [event={}]", event);

        outboxEventWriter.write(
            AggregateType.CHANNEL,
            event.channelId(),
            ChannelDeletedEvent.TOPIC,
            event
        );
    }

    // Message Events

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
