package com.sprint.mission.discodeit.infra.event.listener;

import com.sprint.mission.discodeit.domain.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.domain.service.BinaryContentService;
import com.sprint.mission.discodeit.infra.event.storage.FileStoreEvent;
import com.sprint.mission.discodeit.infra.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.event.EventListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BinaryContentEventListener {

    private static final String KEY_REQUEST_ID = "requestId";

    private static final int RETRY_MAX_ATTEMPTS = 2;
    private static final int RETRY_BACKOFF_DELAY = 1000;
    private static final int RETRY_BACKOFF_MULTIPLIER = 3;

    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentService binaryContentService;

    @Async
    @EventListener
    @Retryable(
        retryFor = Exception.class,
        maxAttempts = RETRY_MAX_ATTEMPTS,
        backoff = @Backoff(delay = RETRY_BACKOFF_DELAY, multiplier = RETRY_BACKOFF_MULTIPLIER)
    )
    public void storeWithRetry(FileStoreEvent event) {
        log.debug("파일 저장 시도: binaryContentId={}", event.binaryContentId());
        binaryContentStorage.put(event.binaryContentId(), event.bytes());
        binaryContentService.updateStatus(event.binaryContentId(), BinaryContentStatus.SUCCESS);
        log.info("파일 저장 완료: binaryContentId={}", event.binaryContentId());
    }

    @Recover
    public void recover(Exception exception, FileStoreEvent event) {
        String requestId = MDC.get(KEY_REQUEST_ID);

        log.error("파일 저장 재시도 모두 실패: binaryContentId={}, requestId={}",
            event.binaryContentId(), requestId, exception);

        binaryContentService.updateStatus(event.binaryContentId(), BinaryContentStatus.FAIL);
    }
}
