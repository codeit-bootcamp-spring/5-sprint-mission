package com.sprint.mission.discodeit.event.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.BinaryContentDTO;
import com.sprint.mission.discodeit.dto.data.NotificationDTO;
import com.sprint.mission.discodeit.dto.request.NotificationCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.BinaryContentUpdatedEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationRequiredTopicListener {

  private final UserRepository userRepository;
  private final ReadStatusRepository readStatusRepository;
  private final NotificationService notificationService;
  private final BinaryContentService binaryContentService;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final ObjectMapper objectMapper;

  @KafkaListener(topics = "discodeit.MessageCreatedEvent")
  public void onMessageCreatedEvent(String kafkaEvent) {
    try {
      MessageCreatedEvent event = objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);

      UUID channelId = event.message().channelId();
      UUID senderId = event.userId();

      List<ReadStatus> channelUsers = readStatusRepository.findAllByChannelIdAndNotificationEnabledTrue(
          channelId);

      channelUsers.stream()
          .filter(readStatus -> !readStatus.getUser().getId().equals(senderId))
          .forEach(readStatus -> {

            NotificationDTO notification = notificationService.createNotification(
                new NotificationCreateRequest(
                    readStatus.getUser().getId(),
                    "보낸 사람 (#" + event.message().author().username() + ")",
                    event.message().content()
                ));
            log.info("알림 전송 완료, message : {}", notification.content());
          });
    } catch (Exception e) {
      log.error("MessageCreatedEvent 처리 실패: {}", kafkaEvent, e);
    }
  }

  @KafkaListener(topics = "discodeit.RoleUpdatedEvent")
  public void onRoleUpdatedEvent(String kafkaEvent) {
    try {
      RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent, RoleUpdatedEvent.class);
      notificationService.createNotification(new NotificationCreateRequest(
          event.userId(),
          "권한이 변경되었습니다.",
          event.oldRole() + " -> " + event.newRole()
      ));
    } catch (Exception e) {
      log.error("RoleUpdatedEvent 처리 실패: {}", kafkaEvent, e);
    }
  }

  @KafkaListener(topics = "discodeit.S3UploadFailedEvent")
  public void onS3UploadFailedEvent(String kafkaEvent) {
    try {
      S3UploadFailedEvent event = objectMapper.readValue(kafkaEvent, S3UploadFailedEvent.class);
      BinaryContentDTO binaryContentDTO = binaryContentService.updateStatus(event.binaryContentId(),
          BinaryContentStatus.FAIL);

      User admin = userRepository.findByUsername("admin").orElseThrow(UserNotFoundException::new);

      String content = "RequestId: " + event.requestId() + "\n" +
          "BinaryContentId: " + event.binaryContentId() + "\n" +
          "Error: " + event.errorMessage();

      notificationService.createNotification(new NotificationCreateRequest(
          admin.getId(),
          "S3 업로드 실패",
          content
      ));

      applicationEventPublisher.publishEvent(
          new BinaryContentUpdatedEvent(binaryContentDTO));
    } catch (Exception e) {
      log.error("S3UploadFailedEvent 처리 실패: {}", kafkaEvent, e);
    }
  }

}
