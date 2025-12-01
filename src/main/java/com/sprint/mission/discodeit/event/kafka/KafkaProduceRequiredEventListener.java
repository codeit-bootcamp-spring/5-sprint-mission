package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.auth.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.binarycontent.BinaryContentUploadFailedEvent;
import com.sprint.mission.discodeit.event.message.MessageCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaProduceRequiredEventListener {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    public void on(MessageCreatedEvent event) {
        sendToKafka(Topic.MESSAGE_CREATED, event, event.messageId().toString());
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    public void on(RoleUpdatedEvent event) {
        sendToKafka(Topic.ROLE_UPDATED, event, event.userId().toString());
    }

    @Async("eventTaskExecutor")
    @EventListener
    public void on(BinaryContentUploadFailedEvent event) {
        sendToKafka(Topic.UPLOAD_FAILED, event, event.binaryContentId().toString());
    }

    private void sendToKafka(Topic topic, Object event, String key) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic.getValue(), key, payload);
            log.debug("Kafka 메시지 전송 완료: topic={}, key={}", topic, key);
        } catch (JsonProcessingException e) {
            log.error("Kafka 메시지 직렬화 실패: topic={}, key={}", topic, key, e);
        }
    }
}
