package com.sprint.mission.discodeit.event.notification;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.event.auth.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.message.MessageCreatedEvent;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRequiredEventListener {

    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final NotificationService notificationService;

    @TransactionalEventListener
    public void on(MessageCreatedEvent event) {
        Message message = messageRepository.findById(event.messageId())
            .orElse(null);

        if (message == null) {
            log.warn("Message not found for notification: messageId={}", event.messageId());
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

        log.debug("Notifications created for message: messageId={}, targetCount={}",
            event.messageId(), notificationTargets.size());
    }

    @TransactionalEventListener
    public void on(RoleUpdatedEvent event) {
        String title = "권한이 변경되었습니다.";
        String content = "%s -> %s".formatted(event.oldRole(), event.newRole());

        notificationService.create(event.userId(), title, content);

        log.debug("Role update notification created: userId={}, {} -> {}",
            event.userId(), event.oldRole(), event.newRole());
    }
}
