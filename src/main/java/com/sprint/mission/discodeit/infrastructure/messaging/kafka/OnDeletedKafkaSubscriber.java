package com.sprint.mission.discodeit.infrastructure.messaging.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.channel.application.ChannelCleanupFacade;
import com.sprint.mission.discodeit.channel.domain.dto.ChannelDeletedEvent;
import com.sprint.mission.discodeit.message.application.MessageCleanupFacade;
import com.sprint.mission.discodeit.message.domain.event.MessageDeletedEvent;
import com.sprint.mission.discodeit.user.application.UserCleanupFacade;
import com.sprint.mission.discodeit.user.domain.dto.UserDeletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OnDeletedKafkaSubscriber {

    private final ChannelCleanupFacade channelCleanupFacade;
    private final MessageCleanupFacade messageCleanupFacade;
    private final UserCleanupFacade userCleanupFacade;

    private final ObjectMapper objectMapper;

    private final ApplicationEventPublisher eventPublisher;

    @RetryableKafkaListener(topics = ChannelDeletedEvent.TOPIC, groupId = "channel-cleanup-group")
    public void onChannelDeletedEvent(String message, @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.debug("ChannelDeletedEvent received: [key={}]", key);

        try {
            ChannelDeletedEvent event = objectMapper.readValue(message, ChannelDeletedEvent.class);

            channelCleanupFacade.cleanup(event);

            log.info("Channel cleanup processed successfully: [channelId={}]", event.channelId());
        } catch (JsonProcessingException exception) {
            log.error("Failed to parse ChannelDeletedEvent: [key={}]", key, exception);

            throw new IllegalArgumentException("Invalid ChannelDeletedEvent JSON", exception);
        }
    }

    @RetryableKafkaListener(topics = MessageDeletedEvent.TOPIC, groupId = "message-cleanup-group")
    public void onMessageDeletedEvent(String message, @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.debug("MessageDeletedEvent received: [key={}]", key);

        try {
            MessageDeletedEvent event = objectMapper.readValue(message, MessageDeletedEvent.class);

            messageCleanupFacade.cleanup(event);

            log.info("Message cleanup processed successfully: [messageId={}]", event.messageId());
        } catch (JsonProcessingException exception) {
            log.error("Failed to parse MessageDeletedEvent: [key={}]", key, exception);

            throw new IllegalArgumentException("Invalid MessageDeletedEvent JSON", exception);
        }
    }

    @RetryableKafkaListener(topics = UserDeletedEvent.TOPIC, groupId = "user-cleanup-group")
    public void onUserDeletedEvent(String message, @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.debug("UserDeletedEvent received: [key={}]", key);

        try {
            UserDeletedEvent event = objectMapper.readValue(message, UserDeletedEvent.class);

            userCleanupFacade.cleanup(event);

            log.info("UserDeletedEvent processed successfully: [userId={}]", event.userId());
        } catch (JsonProcessingException exception) {
            log.error("Failed to parse UserDeletedEvent: [key={}]", key, exception);

            throw new IllegalArgumentException("Invalid UserDeletedEvent JSON", exception);
        }
    }

    @DltHandler
    public void handleDlt(String message,
                          @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                          @Header(KafkaHeaders.EXCEPTION_MESSAGE) String exceptionMessage) {
        log.error("DLT received unprocessable message: topic={}, message={}, cause={}",
            topic, message, exceptionMessage);
    }
}
