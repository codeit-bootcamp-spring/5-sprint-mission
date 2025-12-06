package com.sprint.mission.discodeit.infrastructure.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.global.outbox.AggregateType;
import com.sprint.mission.discodeit.global.outbox.OutboxEventWriter;
import com.sprint.mission.discodeit.infrastructure.outbox.entity.OutboxEvent;
import com.sprint.mission.discodeit.infrastructure.outbox.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxEventWriterImpl implements OutboxEventWriter {

    private final OutboxEventRepository outboxEventRepository;

    private final ObjectMapper objectMapper;

    @Override
    public void write(
        AggregateType aggregateType,
        UUID aggregateId,
        String topic,
        Object event
    ) {
        try {
            log.debug("Outbox 저장 시작: topic={}, aggregateId={}", topic, aggregateId);
            String payload = objectMapper.writeValueAsString(event);
            outboxEventRepository.save(new OutboxEvent(aggregateType, aggregateId, topic, payload));
        } catch (Exception e) {
            log.error("Outbox 저장 실패: topic={}, aggregateId={}", topic, aggregateId, e);
            throw new RuntimeException("이벤트 변환 중 오류 발생", e);
        }
    }
}
