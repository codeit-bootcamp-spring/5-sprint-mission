package com.sprint.mission.discodeit.event.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRequiredTopicListener {
    private final ObjectMapper objectMapper;
    private final ReadStatusRepository readStatusRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;


    @CacheEvict(value ="notification",key = "'noti'")
    @KafkaListener(topics = "discodeit.MessageCreatedEvent" , concurrency = "3")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onMessageCreatedEvent(String kafkaEvent) {
        try {
            MessageCreatedEvent event = objectMapper.readValue(kafkaEvent,
                    MessageCreatedEvent.class);

            log.info("[AFTER_COMMIT] 메시지 생성 커밋 완료, {}", event.id());
            List<ReadStatus> statuses = readStatusRepository.findAllByChannel(event.message().getChannel());
            List<Notification> notifications = new ArrayList<>();

            for (ReadStatus readStatus : statuses) {
                if (!readStatus.isNotificationEnabled()) continue;
                if (readStatus.getUser().getId().equals(event.message().getAuthor().getId())) continue;

                notifications.add(new Notification(
                        readStatus.getUser(),
                        event.message().getAuthor().getUsername() + " (" + readStatus.getChannel().getName() + ")",
                        event.message().getContent()
                ));
            }

            if (!notifications.isEmpty()) {
                notificationRepository.saveAll(notifications); // 배치 저장
                log.info("Saved {} notifications for event {}", notifications.size(), event.id());
            }
        } catch (JsonProcessingException e) {
            log.error("JSON parse error for MessageCreatedEvent: {}", kafkaEvent, e);
            throw new RuntimeException(e);
        } catch (Exception e) {
            log.error("Processing error for MessageCreatedEvent: {}", e.getMessage(), e);
            throw e;
        }
    }

    @CacheEvict(value ="notification",key = "'noti'")
    @KafkaListener(topics = "discodeit.RoleUpdatedEvent",concurrency = "3")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onRoleUpdatedEvent(String kafkaEvent) {
        try {
            RoleUpdatedEvent event = objectMapper.readValue(kafkaEvent,
                    RoleUpdatedEvent.class);

            log.info("[AFTER_COMMIT] 권한 수정 커밋 완료, {}", event.user().getId());
            Notification notifi = new Notification( event.user(),"권한이 변경되었습니다.",
                    ""+event.oldRole()+" -> "+event.newRole());
            notificationRepository.save(notifi);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    //s3대신 테스트하기 좋게 local로 대체
    @KafkaListener(topics = "discodeit.S3UploadFailedEvent", concurrency = "3")
    public void onS3UploadFailedEvent(String kafkaEvent) {
        try {
            S3UploadFailedEvent event = objectMapper.readValue(kafkaEvent,
                    S3UploadFailedEvent.class);

            // 1. RequestId (MDC에서 꺼냄)
            String requestId = MDC.get("requestId");

            // 2. 실패한 BinaryContentId
            UUID failedId = event.binaryContentId();

            // 3. 에러 메시지
            String errorMessage = event.ex().getMessage();

            List<User> users = userRepository.findByRole(Role.ADMIN);
            if(users.isEmpty()) {
                users = userRepository.findByRole(Role.CHANNEL_MANAGER);
            }
            if(users.isEmpty()) {
                users = userRepository.findByRole(Role.USER);
            }
            for(User user : users) {
                UUID  userId = user.getId();
                String title= "S3 파일 저장 실패";
                String content = "RequestId: "+ requestId + " BinaryContentId: "+ failedId + " Error: "+ errorMessage;

                Notification noti= new Notification(userId.toString(), title, content);
                notificationRepository.save(noti);
            }


        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


    }

}
