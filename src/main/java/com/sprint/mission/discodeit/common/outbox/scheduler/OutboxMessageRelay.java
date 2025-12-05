package com.sprint.mission.discodeit.common.outbox.scheduler;

import com.sprint.mission.discodeit.common.outbox.entity.OutboxEvent;
import com.sprint.mission.discodeit.common.outbox.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxMessageRelay {

    private static final int BATCH_SIZE = 50;

    private final OutboxEventRepository outboxEventRepository;

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 1000)
    @SchedulerLock(
        name = "OutboxMessageRelay_publishEvents",
        lockAtLeastFor = "PT0.5S",
        lockAtMostFor = "PT10S"
    )
    public void publishEvents() {
        Pageable pageable = PageRequest.of(0, BATCH_SIZE);
        List<OutboxEvent> events = outboxEventRepository.findAllByOrderByCreatedAtAsc(pageable);

        for (OutboxEvent event : events) {
            try {
                String topic = event.getTopic();
                String key = event.getAggregateId().toString();

                kafkaTemplate.send(topic, key, event.getPayload())
                    .get(3, TimeUnit.SECONDS);

                outboxEventRepository.delete(event);

                log.info("Outbox 이벤트 발행 및 삭제 성공: id={}, topic={}", event.getId(), topic);
            } catch (Exception e) {
                log.error("Outbox 이벤트 발행 실패 (재시도 예정): id={}", event.getId(), e);
            }
        }
    }
}
