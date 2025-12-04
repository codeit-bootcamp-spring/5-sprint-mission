package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
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
            log.info("[Kafka] MessageCreatedEvent 발행 완료: messageId={}", event.getMessageId());
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
}
