package com.sprint.mission.discodeit.infra.storage;

import com.sprint.mission.discodeit.domain.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.domain.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BinaryContentStorageRetryService {

    private static final String KEY_REQUEST_ID = "requestId";

    private static final int RETRY_MAX_ATTEMPTS = 2;
    private static final int RETRY_BACKOFF_DELAY = 1000;
    private static final int RETRY_BACKOFF_MULTIPLIER = 3;

    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentService binaryContentService;
    private final ApplicationEventPublisher eventPublisher;

    @Retryable(
        retryFor = Exception.class,
        maxAttempts = RETRY_MAX_ATTEMPTS,
        backoff = @Backoff(delay = RETRY_BACKOFF_DELAY, multiplier = RETRY_BACKOFF_MULTIPLIER)
    )
    public void storeWithRetry(UUID binaryContentId, byte[] bytes) {
        log.debug("바이너리 콘텐츠 저장 시도: binaryContentId={}", binaryContentId);
        binaryContentStorage.put(binaryContentId, bytes);
        binaryContentService.updateStatus(binaryContentId, BinaryContentStatus.SUCCESS);
        log.info("바이너리 콘텐츠 저장 완료: binaryContentId={}", binaryContentId);
    }

    @Recover
    public void recover(Exception exception, UUID binaryContentId, byte[] bytes) {
        String requestId = MDC.get(KEY_REQUEST_ID);

        log.error("바이너리 콘텐츠 저장 재시도 모두 실패: binaryContentId={}, requestId={}",
            binaryContentId, requestId, exception);

        binaryContentService.updateStatus(binaryContentId, BinaryContentStatus.FAIL);
    }
}
