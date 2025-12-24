package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.UUID;

@Slf4j
// kafka로 대체되어 주석처리
//@Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {

    private final NotificationService notificationService;
    private final ReadStatusRepository readStatusRepository;

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleNotificationMessageCreated(MessageCreatedEvent event) {
        log.info("[NotificationRequiredEventListener] MessageCreatedEvent 수신 - messageId: {}, channelId: {}",
                event.getMessage().getId(), event.getMessage().getChannelId());

        List<ReadStatus> readStatuses = readStatusRepository.findNotifiableByChannel(event.getMessage().getChannelId());

        int notificationCount = 0;

        for (ReadStatus readStatus : readStatuses) {
            UUID receiverId = readStatus.getUser().getId();

            if (receiverId.equals(event.getMessage().getAuthor().getId())) {
                continue;
            }

            String title = event.getMessage().getAuthor().getUsername() + " (#" + event.getChannelName() + ")";
            String content;
            if (event.getMessage().getContent() != null) {
                content = event.getMessage().getContent();
            } else {
                content = "[첨부파일]";
            }

            notificationService.createNotification(receiverId, title, content);
            notificationCount++;

            log.info("[NotificationRequiredEventListener] 알림 생성 완료 - receiverId: {}, title: {}", receiverId, title);
        }

        log.info("[NotificationRequiredEventListener] MessageCreatedEvent 처리 완료 - 알림 생성 수: {}", notificationCount);
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleNotificationRoleUpdated(RoleUpdatedEvent event) {
        log.info("[NotificationRequiredEventListener] RoleUpdatedEvent 수신 - userId: {}, oldRole: {}, newRole: {}",
                event.getUserId(), event.getOldRole(), event.getNewRole());

        String title = "권한이 변경되었습니다.";
        String content = String.format("%s -> %s", event.getOldRole(), event.getNewRole());

        notificationService.createNotification(event.getUserId(), title, content);

        log.info("[NotificationRequiredEventListener] 권한 변경 알림 생성 완료 - userId: {}", event.getUserId());
    }
}