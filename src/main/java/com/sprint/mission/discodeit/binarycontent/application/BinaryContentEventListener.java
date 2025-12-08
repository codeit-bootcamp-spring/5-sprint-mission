package com.sprint.mission.discodeit.binarycontent.application;

import com.sprint.mission.discodeit.binarycontent.domain.BinaryContentCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class BinaryContentEventListener {

    private final BinaryContentStorageProcessor storageProcessor;

    @Async
    @TransactionalEventListener
    public void on(BinaryContentCreatedEvent event) {
        log.info("Received BinaryContentCreatedEvent for binaryContentId: {}", event.binaryContentId());

        storageProcessor.processWithRetry(event);
    }
}
