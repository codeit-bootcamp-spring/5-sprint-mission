package com.sprint.mission.discodeit.event.binarycontent;

import com.sprint.mission.discodeit.storage.BinaryContentStorageRetryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class BinaryContentCreatedEventListener {

    private final BinaryContentStorageRetryService storageRetryService;

    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBinaryContentCreatedEvent(BinaryContentCreatedEvent event) {
        log.debug("BinaryContentCreatedEvent 수신: binaryContentId={}", event.binaryContentId());
        storageRetryService.storeWithRetry(event.binaryContentId(), event.bytes());
    }
}
