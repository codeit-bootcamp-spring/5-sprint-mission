package com.sprint.mission.discodeit.infra.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.domain.event.auth.RoleUpdatedEvent;
import com.sprint.mission.discodeit.domain.event.channel.ChannelDeletedEvent;
import com.sprint.mission.discodeit.domain.event.message.MessageCreatedEvent;
import com.sprint.mission.discodeit.domain.event.message.MessageDeletedEvent;
import com.sprint.mission.discodeit.infra.event.EventTopic;
import com.sprint.mission.discodeit.infra.outbox.entity.AggregateType;
import com.sprint.mission.discodeit.infra.outbox.entity.OutboxEvent;
import com.sprint.mission.discodeit.infra.outbox.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaEventPublisher {

    private final ObjectMapper objectMapper;
    private final OutboxEventRepository outboxEventRepository;

    @TransactionalEventListener
    public void on(MessageCreatedEvent event) {
        saveToOutbox(AggregateType.MESSAGE, event.messageId(), EventTopic.MESSAGE_CREATED, event);
    }

    @TransactionalEventListener
    public void on(RoleUpdatedEvent event) {
        saveToOutbox(AggregateType.USER, event.userId(), EventTopic.ROLE_UPDATED, event);
    }

    @TransactionalEventListener
    public void on(MessageDeletedEvent event) {
        saveToOutbox(AggregateType.MESSAGE, event.messageId(), EventTopic.MESSAGE_DELETED, event);
    }

    @TransactionalEventListener
    public void on(ChannelDeletedEvent event) {
        saveToOutbox(AggregateType.CHANNEL, event.channelId(), EventTopic.CHANNEL_DELETED, event);
    }

    private void saveToOutbox(AggregateType aggregateType, UUID aggregateId, String topic, Object event) {
        try {
            log.debug("Outbox 저장 시작: topic={}, aggregateId={}", topic, aggregateId);

            String payload = objectMapper.writeValueAsString(event);
            outboxEventRepository.save(new OutboxEvent(aggregateType, aggregateId, topic, payload));
        } catch (JsonProcessingException e) {
            log.error("Outbox 저장 실패: topic={}, aggregateId={}", topic, aggregateId, e);
            throw new RuntimeException("이벤트 변환 중 오류 발생", e);
        }
    }
}
