package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.NotificationType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageCreatedEventConsumer {

  private final ObjectMapper objectMapper;
  private final ReadStatusRepository readStatusRepository;
  private final NotificationRepository notificationRepository;

  @KafkaListener(topics = "discodeit.MessageCreatedEvent", groupId = "discodeit-group")
  public void consume(String payload) {
    try {
      MessageCreatedEvent event = objectMapper.readValue(payload, MessageCreatedEvent.class);

      log.info("[Kafka] MessageCreatedEvent consumed: {}", event);

      List<ReadStatus> statuses = readStatusRepository.findByChannelId(event.getChannelId());

      statuses.stream()
              .filter(ReadStatus::getNotificationEnabled)
              .filter(rs -> !rs.getUser()
                               .getId()
                               .equals(event.getAuthorId()))
              .forEach(rs -> {
                Notification notification = Notification.builder()
                                                        .receiver(rs.getUser())
                                                        .title(event.getTitle())
                                                        .content(event.getContent())
                                                        .type(
                                                            NotificationType.MESSAGE_CREATED.name())
                                                        .build();

                notificationRepository.save(notification);
              });

    } catch (Exception e) {
      log.error("[Kafka] Failed to consume MessageCreatedEvent", e);
    }
  }
}