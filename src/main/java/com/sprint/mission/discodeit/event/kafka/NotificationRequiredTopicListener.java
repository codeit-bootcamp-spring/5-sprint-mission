package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRequiredTopicListener {

    private final ObjectMapper objectMapper;
    private final ReadStatusRepository readStatusRepository;
    private final NotificationService notificationService;

    @KafkaListener(topics = "discodeit.MessageCreatedEvent")
    public void onMessageCreatedEvent(String kafkaEvent) {
        try {
            MessageCreatedEvent event = objectMapper.readValue(kafkaEvent, MessageCreatedEvent.class);

            log.info("[KafkaListener] MessageCreatedEvent 수신 - messageId: {}, channelId: {}",
                    event.getMessageId(), event.getChannelId());

            List<ReadStatus> readStatuses = readStatusRepository.findNotifiableByChannel(event.getChannelId());
            int notificationCount = 0;

            for (ReadStatus readStatus : readStatuses) {
                UUID receiverId = readStatus.getUser().getId();

                if (receiverId.equals(event.getAuthorId())) {
                    continue;
                }

                String title = event.getAuthorUsername() + " (#" + event.getChannelName() + ")";
                String content = event.getContent() != null ? event.getContent() : "[첨부파일]";

                notificationService.createNotification(receiverId, title, content);
                notificationCount++;
            }

            log.info("[KafkaListener] MessageCreatedEvent 처리 완료 - 알림 생성 수: {}", notificationCount);

        } catch (JsonProcessingException e) {
            log.error("[KafkaListener] MessageCreatedEvent 포맷 오류, kafkaEvent: {}",kafkaEvent, e);
        } catch (Exception e) {
            log.error("[KafkaListener] MessageCreatedEvent 처리 실패", e);
            throw e;
        }
    }

    @KafkaListener(topics = "discodeit.RoleUpdatedEvent")
    public void onRoleUpdatedEvent(String kafkaEvent) {
        try {
            RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent, RoleUpdatedEvent.class);

            log.info("[KafkaListener] RoleUpdatedEvent 수신 - userId: {}, oldRole: {}, newRole: {}",
                    event.getUserId(), event.getOldRole(), event.getNewRole());

            String title = "권한이 변경되었습니다.";
            String content = String.format("%s -> %s", event.getOldRole(), event.getNewRole());

            notificationService.createNotification(event.getUserId(), title, content);

            log.info("[KafkaListener] 권한 변경 알림 생성 완료 - userId: {}", event.getUserId());

        } catch (JsonProcessingException e) {
            log.error("[KafkaListener] RoleUpdatedEvent 포맷 오류, kafkaEvent: {}",kafkaEvent, e);
        } catch (Exception e) {
            log.error("[KafkaListener] RoleUpdatedEvent 처리 실패", e);
            throw e;
        }
    }

    @KafkaListener(topics = "discodeit.S3UploadFailedEvent")
    public void onS3UploadFailedEvent(String kafkaEvent) {
        try {
            S3UploadFailedEvent event = objectMapper.readValue(kafkaEvent, S3UploadFailedEvent.class);

            log.info("[KafkaListener] S3UploadFailedEvent 수신 - binaryContentId: {}", event.getBinaryContentId());

            String title = "S3 파일업로드 실패 발생";
            String content = String.format(
                    "실패 작업 : S3 파일업로드\n" +
                            "Request ID : %s\n" +
                            "BinaryContentId : %s\n" +
                            "Error : %s",
                    event.getRequestId(),
                    event.getBinaryContentId(),
                    event.getErrorMessage()
            );

            notificationService.notifyAdmins(title, content);

            log.info("[KafkaListener] S3 업로드 실패 알림 전송 완료");

        } catch (JsonProcessingException e) {
            log.error("[KafkaListener] S3UploadFailedEvent 포맷 오류, kafkaEvent: {}",kafkaEvent, e);
        } catch (Exception e) {
            log.error("[KafkaListener] S3UploadFailedEvent 처리 실패", e);
            throw e;
        }
    }
}
