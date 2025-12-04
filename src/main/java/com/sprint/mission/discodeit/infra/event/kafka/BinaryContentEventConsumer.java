package com.sprint.mission.discodeit.infra.event.kafka;

import com.sprint.mission.discodeit.infra.event.binarycontent.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.infra.storage.BinaryContentStorageRetryService;
import com.sprint.mission.discodeit.infra.storage.PendingBinaryContentStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class BinaryContentEventConsumer {

    private final PendingBinaryContentStore pendingStore;
    private final BinaryContentStorageRetryService storageRetryService;

    @KafkaListener(topics = "discodeit.BinaryContentCreatedEvent")
    public void onBinaryContentCreatedEvent(BinaryContentCreatedEvent event) {
        UUID binaryContentId = event.binaryContentId();

        log.debug("BinaryContentCreatedEvent 수신: binaryContentId={}", binaryContentId);

        byte[] bytes = pendingStore.remove(binaryContentId);
        if (bytes == null) {
            log.error("대기 중인 바이너리 콘텐츠를 찾을 수 없음: binaryContentId={}", binaryContentId);
            return;
        }

        storageRetryService.storeWithRetry(binaryContentId, bytes);
    }
}
