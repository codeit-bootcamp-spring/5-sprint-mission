package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaProduceRequiredEventListener {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    public void on(MessageCreatedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("discodeit.MessageCreatedEvent", payload);
            log.info("[Kafka] MessageCreatedEvent 발행 완료: messageId={}", event.getMessage().getId());
        } catch (JsonProcessingException e) {
            log.error("[Kafka] MessageCreatedEvent 발행 실패", e);
        }
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    public void on(RoleUpdatedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("discodeit.RoleUpdatedEvent", payload);
            log.info("[Kafka] RoleUpdatedEvent 발행 완료: userId={}", event.getUserId());
        } catch (JsonProcessingException e) {
            log.error("[Kafka] RoleUpdatedEvent 발행 실패", e);
        }
    }

    @Async("eventTaskExecutor")
    @EventListener
    public void on(S3UploadFailedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("discodeit.S3UploadFailedEvent", payload);
            log.info("[Kafka] S3UploadFailedEvent 발행 완료: binaryContentId={}", event.getBinaryContentId());
        } catch (JsonProcessingException e) {
            log.error("[Kafka] S3UploadFailedEvent 발행 실패", e);
        }
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    public void on(NotificationCreatedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("discodeit.NotificationCreatedEvent", payload);
            log.info("[Kafka] NotificationCreatedEvent 발행 완료");
        } catch (JsonProcessingException e) {
            log.error("[Kafka] NotificationCreatedEvent 발행 실패", e);
        }
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    public void on(BinaryContentUpdatedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("discodeit.BinaryContentUpdatedEvent", payload);
            log.info("[Kafka] BinaryContentUpdatedEvent 발행 완료");
        } catch (JsonProcessingException e) {
            log.error("[Kafka] BinaryContentUpdatedEvent 발행 실패", e);
        }
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    public void on(ChannelCreatedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("discodeit.ChannelCreatedEvent", payload);
            log.info("[Kafka] ChannelCreatedEvent 발행 완료");
        } catch (JsonProcessingException e) {
            log.error("[Kafka] ChannelCreatedEvent 발행 실패", e);
        }
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    public void on(ChannelUpdatedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("discodeit.ChannelUpdatedEvent", payload);
            log.info("[Kafka] ChannelUpdatedEvent 발행 완료");
        } catch (JsonProcessingException e) {
            log.error("[Kafka] ChannelUpdatedEvent 발행 실패", e);
        }
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    public void on(ChannelDeletedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("discodeit.ChannelDeletedEvent", payload);
            log.info("[Kafka] ChannelDeletedEvent 발행 완료");
        } catch (JsonProcessingException e) {
            log.error("[Kafka] ChannelDeletedEvent 발행 실패", e);
        }
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    public void on(UserCreatedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("discodeit.UserCreatedEvent", payload);
            log.info("[Kafka] UserCreatedEvent 발행 완료");
        } catch (JsonProcessingException e) {
            log.error("[Kafka] UserCreatedEvent 발행 실패", e);
        }
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    public void on(UserUpdatedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("discodeit.UserUpdatedEvent", payload);
            log.info("[Kafka] UserUpdatedEvent 발행 완료");
        } catch (JsonProcessingException e) {
            log.error("[Kafka] UserUpdatedEvent 발행 실패", e);
        }
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    public void on(UserDeletedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("discodeit.UserDeletedEvent", payload);
            log.info("[Kafka] UserDeletedEvent 발행 완료");
        } catch (JsonProcessingException e) {
            log.error("[Kafka] UserDeletedEvent 발행 실패", e);
        }
    }
}
