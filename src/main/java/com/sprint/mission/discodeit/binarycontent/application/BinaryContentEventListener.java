package com.sprint.mission.discodeit.binarycontent.application;

import com.sprint.mission.discodeit.binarycontent.domain.event.BinaryContentCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class BinaryContentEventListener {

    private final BinaryContentStorageProcessor storageProcessor;

    @Async
    @TransactionalEventListener
    public void on(BinaryContentCreatedEvent event) {
        storageProcessor.processWithRetry(event);
    }
}
