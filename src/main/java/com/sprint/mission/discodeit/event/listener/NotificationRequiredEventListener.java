package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {

    private final NotificationService notificationService;
    private final ReadStatusRepository readStatusRepository;

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleNotificationMessageCreated(MessageCreatedEvent event) {
        log.info("[EventListener] MessageCreatedEvent 수신 - messageId: {}, channelId: {}",
                event.getMessageId(), event.getChannelId());

        List<ReadStatus> readStatuses = readStatusRepository.findNotifiableByChannel(event.getChannelId());

        int notificationCount = 0;

        for (ReadStatus readStatus : readStatuses) {
            UUID receiverId = readStatus.getUser().getId();

            if (receiverId.equals(event.getAuthorId())) {
                continue;
            }

            String title = event.getAuthorUsername() + " (#" + event.getChannelName() + ")";
            String content;
            if (event.getContent() != null) {
                content = event.getContent();
            } else {
                content = "[첨부파일]";
            }

            notificationService.createNotification(receiverId, title, content);
            notificationCount++;

            log.info("[EventListener] 알림 생성 완료 - receiverId: {}, title: {}", receiverId, title);
        }

        log.info("[EventListener] MessageCreatedEvent 처리 완료 - 알림 생성 수: {}", notificationCount);
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleNotificationRoleUpdated(RoleUpdatedEvent event) {
        log.info("[EventListener] RoleUpdatedEvent 수신 - userId: {}, oldRole: {}, newRole: {}",
                event.getUserId(), event.getOldRole(), event.getNewRole());

        String title = "권한이 변경되었습니다.";
        String content = String.format("%s -> %s", event.getOldRole(), event.getNewRole());

        notificationService.createNotification(event.getUserId(), title, content);

        log.info("[EventListener] 권한 변경 알림 생성 완료 - userId: {}", event.getUserId());
    }
}