package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class BinaryContentCreatedEventListenerTest {

    @Mock
    private BinaryContentStorage binaryContentStorage;

    @InjectMocks
    private BinaryContentCreatedEventListener eventListener;

    @Test
    @DisplayName("handleBinaryContentCreatedEvent - 이벤트 수신 시 바이너리 콘텐츠를 저장한다")
    void handleBinaryContentCreatedEvent_Success() {
        // given
        UUID binaryContentId = UUID.randomUUID();
        byte[] fileBytes = "test-file-data".getBytes();
        BinaryContentCreatedEvent event = new BinaryContentCreatedEvent(binaryContentId, fileBytes);

        // when
        eventListener.handleBinaryContentCreatedEvent(event);

        // then
        then(binaryContentStorage).should().put(binaryContentId, fileBytes);
    }

    @Test
    @DisplayName("handleBinaryContentCreatedEvent - 빈 바이트 배열도 저장한다")
    void handleBinaryContentCreatedEvent_EmptyBytes() {
        // given
        UUID binaryContentId = UUID.randomUUID();
        byte[] emptyBytes = new byte[0];
        BinaryContentCreatedEvent event = new BinaryContentCreatedEvent(binaryContentId, emptyBytes);

        // when
        eventListener.handleBinaryContentCreatedEvent(event);

        // then
        then(binaryContentStorage).should().put(binaryContentId, emptyBytes);
    }

    @Test
    @DisplayName("handleBinaryContentCreatedEvent - 큰 파일도 저장한다")
    void handleBinaryContentCreatedEvent_LargeFile() {
        // given
        UUID binaryContentId = UUID.randomUUID();
        byte[] largeBytes = new byte[1024 * 1024]; // 1MB
        BinaryContentCreatedEvent event = new BinaryContentCreatedEvent(binaryContentId, largeBytes);

        // when
        eventListener.handleBinaryContentCreatedEvent(event);

        // then
        then(binaryContentStorage).should().put(binaryContentId, largeBytes);
    }
}
