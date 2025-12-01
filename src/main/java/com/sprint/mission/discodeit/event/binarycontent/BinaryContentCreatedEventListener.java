package com.sprint.mission.discodeit.event.binarycontent;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class BinaryContentCreatedEventListener {

    private static final String MDC_REQUEST_ID_KEY = "requestId";

    private static final int RETRY_MAX_ATTEMPTS = 2;
    private static final int RETRY_BACKOFF_DELAY = 1000;
    private static final int RETRY_BACKOFF_MULTIPLIER = 3;

    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentService binaryContentService;
    private final ApplicationEventPublisher eventPublisher;

    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBinaryContentCreatedEvent(BinaryContentCreatedEvent event) {
        log.debug("BinaryContentCreatedEvent 수신: binaryContentId={}", event.binaryContentId());

        try {
            storeWithRetry(event.binaryContentId(), event.bytes());
            binaryContentService.updateStatus(event.binaryContentId(), BinaryContentStatus.SUCCESS);
            log.info("바이너리 콘텐츠 저장 완료: binaryContentId={}", event.binaryContentId());
        } catch (Exception e) {
            log.error("바이너리 콘텐츠 저장 최종 실패: binaryContentId={}", event.binaryContentId(), e);
        }
    }

    @Retryable(
        retryFor = Exception.class,
        maxAttempts = RETRY_MAX_ATTEMPTS,
        backoff = @Backoff(delay = RETRY_BACKOFF_DELAY, multiplier = RETRY_BACKOFF_MULTIPLIER)
    )
    public void storeWithRetry(UUID binaryContentId, byte[] bytes) {
        log.debug("바이너리 콘텐츠 저장 시도: binaryContentId={}", binaryContentId);
        binaryContentStorage.put(binaryContentId, bytes);
    }

    @Recover
    public void recover(Exception exception, UUID binaryContentId, byte[] bytes) {
        String requestId = MDC.get(MDC_REQUEST_ID_KEY);

        log.error("바이너리 콘텐츠 저장 재시도 모두 실패: binaryContentId={}, requestId={}",
            binaryContentId, requestId, exception);

        binaryContentService.updateStatus(binaryContentId, BinaryContentStatus.FAIL);

        eventPublisher.publishEvent(new BinaryContentUploadFailedEvent(
            binaryContentId,
            requestId,
            exception.getMessage()
        ));

        throw new RuntimeException("바이너리 콘텐츠 저장 실패", exception);
    }
}
