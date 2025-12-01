package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.event.auth.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.binarycontent.BinaryContentUploadFailedEvent;
import com.sprint.mission.discodeit.event.message.MessageCreatedEvent;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRequiredTopicListener {

    private final NotificationService notificationService;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "discodeit.MessageCreatedEvent")
    public void onMessageCreatedEvent(String kafkaEvent) {
        try {
            MessageCreatedEvent event = objectMapper.readValue(kafkaEvent,
                MessageCreatedEvent.class);

            Message message = messageRepository.findById(event.messageId())
                .orElse(null);

            if (message == null) {
                log.warn("알림 대상 메시지를 찾을 수 없음: messageId={}", event.messageId());
                return;
            }

            List<ReadStatus> notificationTargets = readStatusRepository
                .findAllByChannelIdWithNotificationEnabled(
                    message.getChannel().getId(),
                    message.getAuthor().getId()
                );

            String channelName = message.getChannel().getName();
            String title = "%s (#%s)".formatted(
                message.getAuthor().getUsername(),
                channelName != null ? channelName : "DM"
            );
            String content = message.getContent();

            for (ReadStatus readStatus : notificationTargets) {
                notificationService.create(
                    readStatus.getUser().getId(),
                    title,
                    content
                );
            }

            log.debug("메시지 알림 생성 완료: messageId={}, targetCount={}",
                event.messageId(), notificationTargets.size());
        } catch (JsonProcessingException e) {
            log.error("MessageCreatedEvent 역직렬화 실패: {}", kafkaEvent, e);
        }
    }

    @KafkaListener(topics = "discodeit.RoleUpdatedEvent")
    public void onRoleUpdatedEvent(String kafkaEvent) {
        try {
            RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent, RoleUpdatedEvent.class);

            String title = "권한이 변경되었습니다.";
            String content = "%s -> %s".formatted(event.oldRole(), event.newRole());

            notificationService.create(event.userId(), title, content);

            log.debug("권한 변경 알림 생성 완료: userId={}, {} -> {}",
                event.userId(), event.oldRole(), event.newRole());
        } catch (JsonProcessingException e) {
            log.error("RoleUpdatedEvent 역직렬화 실패: {}", kafkaEvent, e);
        }
    }

    @KafkaListener(topics = "discodeit.S3UploadFailedEvent")
    public void onS3UploadFailedEvent(String kafkaEvent) {
        try {
            BinaryContentUploadFailedEvent event = objectMapper.readValue(kafkaEvent,
                BinaryContentUploadFailedEvent.class);

            log.warn("S3 업로드 실패: binaryContentId={}, requestId={}, error={}",
                event.binaryContentId(), event.requestId(), event.errorMessage());
        } catch (JsonProcessingException e) {
            log.error("S3UploadFailedEvent 역직렬화 실패: {}", kafkaEvent, e);
        }
    }
}
