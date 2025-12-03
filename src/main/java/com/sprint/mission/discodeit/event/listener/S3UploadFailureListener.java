package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3UploadFailureListener {
    private final NotificationService notificationService;

    @Async("eventTaskExecutor")
    @EventListener
    public void handleS3UploadFailure(S3UploadFailedEvent event) {
        log.info("[S3UploadFailureListener] S3 업로드 실패 이벤트 수신: {}", event.getBinaryContentId());

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

        try {
            notificationService.notifyAdmins(title, content);
            log.info("[S3UploadFailureListener] 관리자 알림 전송 완료: {}", event.getBinaryContentId());
        } catch (Exception e) {
            log.error("[S3UploadFailureListener] 관리자 알림 전송 실패: {}", event.getBinaryContentId(), e);
        }
    }
}
