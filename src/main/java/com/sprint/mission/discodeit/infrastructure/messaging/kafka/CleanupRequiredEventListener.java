package com.sprint.mission.discodeit.infrastructure.messaging.kafka;

import com.sprint.mission.discodeit.channel.application.ChannelCleanupFacade;
import com.sprint.mission.discodeit.channel.domain.event.ChannelDeletedEvent;
import com.sprint.mission.discodeit.message.application.MessageCleanupFacade;
import com.sprint.mission.discodeit.message.domain.event.MessageDeletedEvent;
import com.sprint.mission.discodeit.user.application.UserCleanupFacade;
import com.sprint.mission.discodeit.user.domain.event.UserDeletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CleanupRequiredEventListener {

    private final ChannelCleanupFacade channelCleanupFacade;
    private final MessageCleanupFacade messageCleanupFacade;
    private final UserCleanupFacade userCleanupFacade;

    @RetryableKafkaListener(topics = ChannelDeletedEvent.TOPIC, groupId = "channel-cleanup-group")
    public void onChannelDeletedEvent(ChannelDeletedEvent event) {
        channelCleanupFacade.cleanup(event);
    }

    @RetryableKafkaListener(topics = MessageDeletedEvent.TOPIC, groupId = "message-cleanup-group")
    public void onMessageDeletedEvent(MessageDeletedEvent event) {
        messageCleanupFacade.cleanup(event);
    }

    @RetryableKafkaListener(topics = UserDeletedEvent.TOPIC, groupId = "user-cleanup-group")
    public void onUserDeletedEvent(UserDeletedEvent event) {
        userCleanupFacade.cleanup(event);
    }

    @DltHandler
    public void handleDlt(
        byte[] payload,
        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
        @Header(KafkaHeaders.RECEIVED_KEY) String key,
        @Header(KafkaHeaders.EXCEPTION_MESSAGE) String exceptionMessage
    ) {
        log.error("DLT Received: topic={}, key={}, error={}", topic, key, exceptionMessage);
    }
}
