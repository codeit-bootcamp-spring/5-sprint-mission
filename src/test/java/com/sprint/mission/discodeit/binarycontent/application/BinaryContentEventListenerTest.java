package com.sprint.mission.discodeit.binarycontent.application;

import com.sprint.mission.discodeit.binarycontent.domain.event.BinaryContentCreatedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("BinaryContentEventListener 단위 테스트")
class BinaryContentEventListenerTest {

    @Mock
    private BinaryContentStorageProcessor storageProcessor;

    @InjectMocks
    private BinaryContentEventListener listener;

    @Test
    @DisplayName("BinaryContentCreatedEvent 수신 시 storageProcessor.processWithRetry 호출")
    void on_withBinaryContentCreatedEvent_callsStorageProcessor() {
        // given
        UUID binaryContentId = UUID.randomUUID();
        byte[] bytes = "test content".getBytes();
        BinaryContentCreatedEvent event = new BinaryContentCreatedEvent(binaryContentId, bytes);

        // when
        listener.on(event);

        // then
        then(storageProcessor).should().processWithRetry(event);
    }

    @Test
    @DisplayName("빈 바이트 배열 이벤트도 정상 처리")
    void on_withEmptyBytes_callsStorageProcessor() {
        // given
        UUID binaryContentId = UUID.randomUUID();
        byte[] emptyBytes = new byte[0];
        BinaryContentCreatedEvent event = new BinaryContentCreatedEvent(binaryContentId, emptyBytes);

        // when
        listener.on(event);

        // then
        then(storageProcessor).should().processWithRetry(event);
    }
}
