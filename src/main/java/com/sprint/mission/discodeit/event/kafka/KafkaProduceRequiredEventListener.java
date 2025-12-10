package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.event.BinaryContentFailEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
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
      log.info("Kafka sent: MessageCreatedEvent");
    } catch (Exception e) {
      log.error("Kafka publish failed: {}", e.getMessage());
    }
  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener
  public void on(RoleUpdatedEvent event) {
    try {
      String payload = objectMapper.writeValueAsString(event);
      kafkaTemplate.send("discodeit.RoleUpdatedEvent", payload);
      log.info("Kafka sent: RoleUpdatedEvent");
    } catch (Exception e) {
      log.error("Kafka publish failed: {}", e.getMessage());
    }
  }

  @Async("eventTaskExecutor")
  @EventListener
  public void on(BinaryContentFailEvent event) {
    try {
      String payload = objectMapper.writeValueAsString(event);
      kafkaTemplate.send("discodeit.BinaryContentFailEvent", payload);
      log.error("Kafka sent: BinaryContentFailEvent (FAIL EVENT)");
    } catch (Exception e) {
      log.error("Kafka publish failed: {}", e.getMessage());
    }
  }


  @Async("eventTaskExecutor")
  @EventListener
  public void on(BinaryContentCreatedEvent event) {
    try {
      String payload = objectMapper.writeValueAsString(event);
      kafkaTemplate.send("discodeit.BinaryContentCreatedEvent", payload);
      log.error("Kafka sent: BinaryContentCreatedEvent");
    } catch (Exception e) {
      log.error("Kafka publish failed: {}", e.getMessage());
    }
  }
}
