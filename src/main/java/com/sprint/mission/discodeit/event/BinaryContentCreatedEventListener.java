package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class BinaryContentCreatedEventListener {

    private final BinaryContentStorage binaryContentStorage;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBinaryContentCreatedEvent(BinaryContentCreatedEvent event) {
        log.debug("BinaryContentCreatedEvent 수신: binaryContentId={}", event.binaryContentId());

        binaryContentStorage.put(event.binaryContentId(), event.bytes());

        log.info("바이너리 콘텐츠 저장 완료: binaryContentId={}", event.binaryContentId());
    }
}
