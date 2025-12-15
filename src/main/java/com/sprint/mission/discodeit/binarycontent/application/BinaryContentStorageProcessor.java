package com.sprint.mission.discodeit.binarycontent.application;

import com.sprint.mission.discodeit.binarycontent.domain.BinaryContentStatus;
import com.sprint.mission.discodeit.binarycontent.domain.BinaryContentStorage;
import com.sprint.mission.discodeit.binarycontent.domain.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.notification.application.NotificationService;
import com.sprint.mission.discodeit.user.domain.Role;
import com.sprint.mission.discodeit.user.domain.User;
import com.sprint.mission.discodeit.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BinaryContentStorageProcessor {

    private static final String TASK_NAME = "BinaryContentStorage";
    private static final String REQUEST_ID_KEY = "requestId";

    private final BinaryContentService binaryContentService;
    private final BinaryContentStorage binaryContentStorage;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @Retryable(
        retryFor = Exception.class,
        maxAttemptsExpression = "${discodeit.storage.retry.max-attempts}",
        backoff = @Backoff(
            delayExpression = "${discodeit.storage.retry.backoff-delay}",
            multiplierExpression = "${discodeit.storage.retry.backoff-multiplier}")
    )
    public void processWithRetry(BinaryContentCreatedEvent event) {
        log.debug("Attempting storage upload: [binaryContentId={}]", event.binaryContentId());

        binaryContentStorage.put(event.binaryContentId(), event.bytes());
        binaryContentService.updateStatus(event.binaryContentId(), BinaryContentStatus.SUCCESS);

        log.info("Storage upload success: [binaryContentId={}]", event.binaryContentId());
    }

    @Recover
    public void recover(Exception exception, BinaryContentCreatedEvent event) {
        log.error("Binary content storage failed after all retries: [binaryContentId={}]",
            event.binaryContentId(), exception);

        binaryContentService.updateStatus(event.binaryContentId(), BinaryContentStatus.FAIL);
        notifyAdmins(event, exception);
    }

    private void notifyAdmins(BinaryContentCreatedEvent event, Exception exception) {
        String requestId = MDC.get(REQUEST_ID_KEY);
        String title = "파일 업로드 실패";
        String content = buildNotificationContent(event, exception, requestId);

        List<User> admins = userRepository.findAllByRole(Role.ADMIN);
        for (User admin : admins) {
            try {
                notificationService.create(admin.getId(), title, content);
                log.debug("Admin notified about storage failure: [adminId={}, binaryContentId={}]",
                    admin.getId(), event.binaryContentId());
            } catch (Exception e) {
                log.warn("Failed to notify admin: [adminId={}, binaryContentId={}]",
                    admin.getId(), event.binaryContentId(), e);
            }
        }
    }

    private String buildNotificationContent(
        BinaryContentCreatedEvent event,
        Exception exception,
        String requestId
    ) {
        return String.format(
            "Task: %s%nRequestId: %s%nBinaryContentId: %s%nError: %s",
            TASK_NAME,
            requestId != null ? requestId : "N/A",
            event.binaryContentId(),
            exception.getMessage()
        );
    }
}
