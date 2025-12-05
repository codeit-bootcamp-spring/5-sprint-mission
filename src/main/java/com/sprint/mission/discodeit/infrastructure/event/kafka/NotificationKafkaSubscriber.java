package com.sprint.mission.discodeit.infrastructure.event.kafka;

import com.sprint.mission.discodeit.domain.auth.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.domain.message.entity.Message;
import com.sprint.mission.discodeit.domain.message.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.domain.message.repository.MessageRepository;
import com.sprint.mission.discodeit.domain.notification.service.NotificationService;
import com.sprint.mission.discodeit.domain.readstatus.entity.ReadStatus;
import com.sprint.mission.discodeit.domain.readstatus.repository.ReadStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationKafkaSubscriber {

    private final NotificationService notificationService;

    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;

    @KafkaListener(topics = "discodeit.MessageCreatedEvent", groupId = "notification-group")
    public void onMessageCreated(MessageCreatedEvent event) {
        Message message = messageRepository.findById(event.messageId()).orElse(null);
        if (message == null) {
            log.warn("알림 대상 메시지를 찾을 수 없음: messageId={}", event.messageId());
            return;
        }

        List<ReadStatus> targets = readStatusRepository.findAllByChannelIdWithNotificationEnabled(
            message.getChannel().getId(), message.getAuthor().getId());

        String title = "%s (#%s)".formatted(
            message.getAuthor().getUsername(),
            message.getChannel().getName() != null ? message.getChannel().getName() : "DM"
        );

        targets.forEach(target ->
            notificationService.create(target.getUser().getId(), title, message.getContent())
        );
    }

    @KafkaListener(topics = "discodeit.RoleUpdatedEvent", groupId = "notification-group")
    public void onRoleUpdated(RoleUpdatedEvent event) {
        String title = "권한이 변경되었습니다.";
        String content = "%s -> %s".formatted(event.oldRole(), event.newRole());

        notificationService.create(event.userId(), title, content);
    }
}
