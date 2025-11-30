package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.binarycontent.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.event.binarycontent.BinaryContentCreatedEventListener;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

@ExtendWith(MockitoExtension.class)
class BinaryContentCreatedEventListenerTest {

    @Mock
    private BinaryContentStorage binaryContentStorage;

    @Mock
    private BinaryContentService binaryContentService;

    @InjectMocks
    private BinaryContentCreatedEventListener eventListener;

    @Test
    @DisplayName("handleBinaryContentCreatedEvent - 저장 성공 시 상태를 SUCCESS로 업데이트한다")
    void handleBinaryContentCreatedEvent_Success() {
        // given
        UUID binaryContentId = UUID.randomUUID();
        byte[] fileBytes = "test-file-data".getBytes();
        BinaryContentCreatedEvent event = new BinaryContentCreatedEvent(binaryContentId, fileBytes);

        given(binaryContentStorage.put(binaryContentId, fileBytes)).willReturn(binaryContentId);

        // when
        eventListener.handleBinaryContentCreatedEvent(event);

        // then
        then(binaryContentStorage).should().put(binaryContentId, fileBytes);
        then(binaryContentService).should().updateStatus(binaryContentId, BinaryContentStatus.SUCCESS);
    }

    @Test
    @DisplayName("handleBinaryContentCreatedEvent - 저장 실패 시 상태를 FAIL로 업데이트한다")
    void handleBinaryContentCreatedEvent_Failure() {
        // given
        UUID binaryContentId = UUID.randomUUID();
        byte[] fileBytes = "test-file-data".getBytes();
        BinaryContentCreatedEvent event = new BinaryContentCreatedEvent(binaryContentId, fileBytes);

        willThrow(new RuntimeException("Storage error"))
            .given(binaryContentStorage).put(binaryContentId, fileBytes);

        // when
        eventListener.handleBinaryContentCreatedEvent(event);

        // then
        then(binaryContentStorage).should().put(binaryContentId, fileBytes);
        then(binaryContentService).should().updateStatus(binaryContentId, BinaryContentStatus.FAIL);
    }

    @Test
    @DisplayName("handleBinaryContentCreatedEvent - 빈 바이트 배열도 저장하고 SUCCESS로 업데이트한다")
    void handleBinaryContentCreatedEvent_EmptyBytes() {
        // given
        UUID binaryContentId = UUID.randomUUID();
        byte[] emptyBytes = new byte[0];
        BinaryContentCreatedEvent event = new BinaryContentCreatedEvent(binaryContentId, emptyBytes);

        given(binaryContentStorage.put(binaryContentId, emptyBytes)).willReturn(binaryContentId);

        // when
        eventListener.handleBinaryContentCreatedEvent(event);

        // then
        then(binaryContentStorage).should().put(binaryContentId, emptyBytes);
        then(binaryContentService).should().updateStatus(binaryContentId, BinaryContentStatus.SUCCESS);
    }
}
