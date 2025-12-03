package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.binarycontent.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.storage.BinaryContentStorageRetryService;
import com.sprint.mission.discodeit.storage.PendingBinaryContentStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class BinaryContentConsumer {

    private final PendingBinaryContentStore pendingStore;
    private final BinaryContentStorageRetryService storageRetryService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "discodeit.BinaryContentCreatedEvent")
    public void onBinaryContentCreatedEvent(String kafkaEvent) {
        try {
            BinaryContentCreatedEvent event = objectMapper.readValue(kafkaEvent, BinaryContentCreatedEvent.class);
            UUID binaryContentId = event.binaryContentId();

            log.debug("BinaryContentCreatedEvent 수신: binaryContentId={}", binaryContentId);

            byte[] bytes = pendingStore.remove(binaryContentId);
            if (bytes == null) {
                log.error("대기 중인 바이너리 콘텐츠를 찾을 수 없음: binaryContentId={}", binaryContentId);
                return;
            }

            storageRetryService.storeWithRetry(binaryContentId, bytes);
        } catch (JsonProcessingException e) {
            log.error("BinaryContentCreatedEvent 역직렬화 실패: {}", kafkaEvent, e);
        }
    }
}
