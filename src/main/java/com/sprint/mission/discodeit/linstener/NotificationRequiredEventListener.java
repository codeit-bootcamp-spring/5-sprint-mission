package com.sprint.mission.discodeit.linstener;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.sprint.mission.discodeit.entity.ChannelType.*;
import static com.sprint.mission.discodeit.entity.Role.ADMIN;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {

    private final NotificationRepository notificationRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;

    private final ChannelService channelService;
    private final MessageService messageService;
    private final NotificationService notificationService;

    @Async(value = "eventExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(MessageCreatedEvent event) {
        UUID channelId = event.getChannelId();
        UUID messageId = event.getMessageId();
        MessageDto message = messageService.find(messageId);
        ChannelDto channel = channelService.find(channelId);

        List<ReadStatus> targets =
                readStatusRepository.findAllByChannelIdAndNotificationEnabledTrue(channelId);

        Set<UUID> receiverIds = new HashSet<>();
        for (ReadStatus readStatus : targets) {
            if (readStatus.getUser().getId().equals(event.getSenderId())) {
                continue;
            }

            UUID userId = readStatus.getUser().getId();
            receiverIds.add(userId);
        }

        String username = message.author().username();
        String concat_string = channel.type().equals(PUBLIC) ? String.format(" (#%s)", channel.name()) : "";
        String title = username.concat(concat_string);

        notificationService.create(receiverIds, title, message.content());
    }

    @Async(value = "eventExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(RoleUpdatedEvent event) {
        log.info("[이벤트 수신] RoleUpdatedEvent: userId={}, {} -> {}",
                event.getUserId(), event.getOldRole(), event.getNewRole());

        UUID userId = event.getUserId();
        String title = "권한이 변경되었습니다.";
        String content = event.getOldRole() + " -> " + event.getNewRole();

        notificationService.create(Set.of(userId), title, content);
    }

    @Async("eventExecutor")
    @EventListener
    public void on(FailureNotificationEvent event) {

        try {
            log.error("[비동기 실패 이벤트 수신] {}", event.getTaskName());

            String title = "S3 파일 업로드 실패 " + event.getTaskName();

            String content = """
                RequestId: %s
                BinaryContentId: %s
                Error: %s
                """.formatted(
                    event.getRequestId(),
                    event.getBinaryContentId(),
                    event.getErrorMessage()
            );

            log.info("[FailureEvent] building adminIds...");
            List<User> admins = userRepository.findByRoleIs(ADMIN);
            Set<UUID> adminIds = admins.stream()
                    .map(User::getId)
                    .collect(Collectors.toSet());
            log.info("[FailureEvent] adminIds = {}", adminIds);

            notificationService.create(adminIds, title, content);

        } catch (Exception e) {
            log.error("[FailureEvent 처리 중 예외 발생] {}", e.getMessage(), e);
        }
    }
}