package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.auth.AuthAuditEvent;
import com.sprint.mission.discodeit.event.auth.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.binarycontent.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.event.binarycontent.BinaryContentUploadFailedEvent;
import com.sprint.mission.discodeit.event.channel.ChannelDeletedEvent;
import com.sprint.mission.discodeit.event.message.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.message.MessageDeletedEvent;
import com.sprint.mission.discodeit.event.user.UserDeletedEvent;
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
public class KafkaEventPublishListener {

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
    @TransactionalEventListener
    public void on(BinaryContentCreatedEvent event) {
        sendToKafka(Topic.BINARY_CONTENT_CREATED, event, event.binaryContentId().toString());
    }

    @Async("eventTaskExecutor")
    @EventListener
    public void on(BinaryContentUploadFailedEvent event) {
        sendToKafka(Topic.UPLOAD_FAILED, event, event.binaryContentId().toString());
    }

    @Async("eventTaskExecutor")
    @EventListener
    public void on(MessageDeletedEvent event) {
        sendToKafka(Topic.MESSAGE_DELETED, event, event.messageId().toString());
    }

    @Async("eventTaskExecutor")
    @EventListener
    public void on(ChannelDeletedEvent event) {
        sendToKafka(Topic.CHANNEL_DELETED, event, event.channelId().toString());
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    public void on(UserDeletedEvent event) {
        sendToKafka(Topic.USER_DELETED, event, event.userId().toString());
    }

    @Async("eventTaskExecutor")
    @EventListener
    public void on(AuthAuditEvent event) {
        sendToKafka(Topic.AUTH_AUDIT, event, event.userId().toString());
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
