package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.NotificationType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.event.BinaryContentFailEvent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BinaryContentEventConsumer {

  private final ObjectMapper objectMapper;
  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentRepository binaryContentRepository;
  private final NotificationRepository notificationRepository;
  private final UserRepository userRepository;

  @KafkaListener(topics = "discodeit.BinaryContentFailEvent", groupId = "discodeit-group")
  public void consumeFail(String payload) {
    try {
      BinaryContentFailEvent event =
          objectMapper.readValue(payload, BinaryContentFailEvent.class);

      log.error("[Kafka] BinaryContentFailEvent consumed: {}", event);

      updateStatusToFail(event.getBinaryContentId(), null);

    } catch (Exception e) {
      log.error("[Kafka] Failed to consume BinaryContentFailEvent", e);
    }
  }

  @KafkaListener(topics = "discodeit.BinaryContentCreatedEvent", groupId = "discodeit-group")
  public void consumeCreated(String payload) {
    try {
      BinaryContentCreatedEvent event =
          objectMapper.readValue(payload, BinaryContentCreatedEvent.class);

      log.info("[Kafka] BinaryContentCreatedEvent consumed: {}", event);

      UUID id = event.getBinaryContentId();
      binaryContentStorage.put(id, event.getBytes());
      updateStatusToSuccess(id, event.getUserId());

    } catch (Exception e) {
      log.error("[Kafka] Failed BinaryContentCreatedEvent → fallback to FAIL", e);
    }
  }

  private void updateStatusToSuccess(UUID binaryContentId, UUID userId) {
    BinaryContent bc = binaryContentRepository.findById(binaryContentId)
                                              .orElseThrow(() -> new BinaryContentNotFoundException(
                                                  binaryContentId));

    bc.updateStatus(BinaryContentStatus.SUCCESS.name());
    binaryContentRepository.save(bc);

    User user = userRepository.findById(userId)
                              .orElse(null);
    if (user != null) {

      String requestId = MDC.get("requestId");
      Notification notification = Notification.builder()
                                              .receiver(user)
                                              .title("[Binary 업로드 성공]")
                                              .content("""
                                                  RequestId: %s
                                                  BinaryContentId: %s
                                                  """.formatted(requestId, binaryContentId))
                                              .type(NotificationType.S3_UPLOAD_FAILED.name())
                                              .build();
      notificationRepository.save(notification);
    }
  }

  private void updateStatusToFail(UUID binaryContentId, User user) {
    BinaryContent bc = binaryContentRepository.findById(binaryContentId)
                                              .orElseThrow(() -> new BinaryContentNotFoundException(
                                                  binaryContentId));

    bc.updateStatus(BinaryContentStatus.FAIL.name());
    binaryContentRepository.save(bc);

    if (user != null) {

      String requestId = MDC.get("requestId");

      Notification notification = Notification.builder()
                                              .receiver(user)
                                              .title("[Binary 업로드 실패]")
                                              .content("""
                                                  RequestId: %s
                                                  BinaryContentId: %s
                                                  """.formatted(requestId, binaryContentId))
                                              .type(NotificationType.S3_UPLOAD_FAILED.name())
                                              .build();

      notificationRepository.save(notification);
    }
  }
}