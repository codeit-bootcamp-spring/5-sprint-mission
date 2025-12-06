package com.sprint.mission.discodeit.binarycontent.application;

import com.sprint.mission.discodeit.binarycontent.domain.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.binarycontent.domain.BinaryContentStatus;
import com.sprint.mission.discodeit.binarycontent.domain.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BinaryContentStorageProcessor {

    private final BinaryContentService binaryContentService;
    private final BinaryContentStorage binaryContentStorage;

    @Retryable(
        retryFor = Exception.class,
        maxAttemptsExpression = "${discodeit.storage.retry.max-attempts}",
        backoff = @Backoff(
            delayExpression = "${discodeit.storage.retry.backoff-delay}",
            multiplierExpression = "${discodeit.storage.retry.backoff-multiplier}")
    )
    public void processWithRetry(BinaryContentCreatedEvent event) {
        log.debug("Attempting storage upload for id={}", event.binaryContentId());
        binaryContentStorage.put(event.binaryContentId(), event.bytes());
        binaryContentService.updateStatus(event.binaryContentId(), BinaryContentStatus.SUCCESS);
        log.info("Storage upload success: id={}", event.binaryContentId());
    }

    @Recover
    public void recover(Exception exception, BinaryContentCreatedEvent event) {
        binaryContentService.updateStatus(event.binaryContentId(), BinaryContentStatus.FAIL);
        log.error("Binary content storage failed after all retries: id={}", event.binaryContentId(), exception);
    }
}
