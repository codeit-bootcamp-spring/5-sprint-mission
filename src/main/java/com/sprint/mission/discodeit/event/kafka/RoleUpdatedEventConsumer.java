package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.enums.NotificationType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoleUpdatedEventConsumer {

  private final ObjectMapper objectMapper;
  private final UserRepository userRepository;
  private final NotificationRepository notificationRepository;

  @KafkaListener(topics = "discodeit.RoleUpdatedEvent", groupId = "discodeit-group")
  public void consume(String payload) {
    try {
      RoleUpdatedEvent event = objectMapper.readValue(payload, RoleUpdatedEvent.class);

      log.info("[Kafka] RoleUpdatedEvent consumed: {}", event);

      User user = userRepository.findById(event.getUserId())
                                .orElse(null);
      if (user == null) {
        return;
      }

      Notification notification = Notification.builder()
                                              .receiver(user)
                                              .title("권한이 변경되었습니다.")
                                              .content(String.format("%s → %s", event.getOldRole(),
                                                  event.getNewRole()))
                                              .type(NotificationType.ROLE_UPDATED.name())
                                              .build();

      notificationRepository.save(notification);

    } catch (Exception e) {
      log.error("[Kafka] Failed to consume RoleUpdatedEvent", e);
    }
  }
}