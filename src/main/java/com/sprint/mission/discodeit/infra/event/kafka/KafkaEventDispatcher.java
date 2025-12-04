package com.sprint.mission.discodeit.infra.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.infra.event.auth.RoleUpdatedEvent;
import com.sprint.mission.discodeit.infra.event.cache.CacheEvictEvent;
import com.sprint.mission.discodeit.infra.event.channel.ChannelDeletedEvent;
import com.sprint.mission.discodeit.infra.event.message.MessageCreatedEvent;
import com.sprint.mission.discodeit.infra.event.message.MessageDeletedEvent;
import com.sprint.mission.discodeit.infra.event.user.UserDeletedEvent;
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
public class KafkaEventDispatcher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Async
    @TransactionalEventListener
    public void on(MessageCreatedEvent event) {
        sendToKafka(Topic.MESSAGE_CREATED, event, event.messageId().toString());
    }

    @Async
    @TransactionalEventListener
    public void on(RoleUpdatedEvent event) {
        sendToKafka(Topic.ROLE_UPDATED, event, event.userId().toString());
    }

    @Async
    @EventListener
    public void on(MessageDeletedEvent event) {
        sendToKafka(Topic.MESSAGE_DELETED, event, event.messageId().toString());
    }

    @Async
    @EventListener
    public void on(ChannelDeletedEvent event) {
        sendToKafka(Topic.CHANNEL_DELETED, event, event.channelId().toString());
    }

    @Async
    @TransactionalEventListener
    public void on(UserDeletedEvent event) {
        sendToKafka(Topic.USER_DELETED, event, event.userId().toString());
    }

    @Async
    @TransactionalEventListener
    public void on(CacheEvictEvent event) {
        sendToKafka(Topic.CACHE_EVICT, event, event.cacheName() + ":" + event.key());
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
