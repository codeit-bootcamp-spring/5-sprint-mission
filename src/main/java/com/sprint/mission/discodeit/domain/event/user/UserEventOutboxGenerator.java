package com.sprint.mission.discodeit.domain.event.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.infra.event.EventTopic;
import com.sprint.mission.discodeit.infra.outbox.entity.AggregateType;
import com.sprint.mission.discodeit.infra.outbox.entity.OutboxEvent;
import com.sprint.mission.discodeit.infra.outbox.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventOutboxGenerator {

    private final OutboxEventRepository outboxEventRepository;

    private final ObjectMapper objectMapper;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleUserDeleted(UserDeletedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);

            OutboxEvent outboxEvent = new OutboxEvent(
                AggregateType.USER,
                event.userId(),
                EventTopic.USER_DELETED,
                payload
            );

            outboxEventRepository.save(outboxEvent);

        } catch (JsonProcessingException e) {
            log.error("Outbox 저장 실패 (JSON 변환 오류)", e);
            throw new RuntimeException(e);
        }
    }
}
