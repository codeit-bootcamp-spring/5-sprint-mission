package com.sprint.mission.discodeit.event.binarycontent;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

@ExtendWith(MockitoExtension.class)
class BinaryContentCreatedEventListenerTest {

    @Mock
    private BinaryContentStorage binaryContentStorage;

    @Mock
    private BinaryContentService binaryContentService;

    @InjectMocks
    private BinaryContentCreatedEventListener listener;

    @Test
    @DisplayName("바이너리 콘텐츠 저장 성공 시 상태를 SUCCESS로 업데이트")
    void handleBinaryContentCreatedEvent_Success() {
        // given
        UUID binaryContentId = UUID.randomUUID();
        byte[] bytes = "test content".getBytes();
        BinaryContentCreatedEvent event = new BinaryContentCreatedEvent(binaryContentId, bytes);

        // when
        listener.handleBinaryContentCreatedEvent(event);

        // then
        then(binaryContentStorage).should().put(binaryContentId, bytes);
        then(binaryContentService).should().updateStatus(binaryContentId, BinaryContentStatus.SUCCESS);
    }

    @Test
    @DisplayName("바이너리 콘텐츠 저장 실패 시 상태를 FAIL로 업데이트")
    void handleBinaryContentCreatedEvent_Failure() {
        // given
        UUID binaryContentId = UUID.randomUUID();
        byte[] bytes = "test content".getBytes();
        BinaryContentCreatedEvent event = new BinaryContentCreatedEvent(binaryContentId, bytes);

        willThrow(new RuntimeException("Storage error"))
            .given(binaryContentStorage).put(binaryContentId, bytes);

        // when
        listener.handleBinaryContentCreatedEvent(event);

        // then
        then(binaryContentStorage).should().put(binaryContentId, bytes);
        then(binaryContentService).should().updateStatus(binaryContentId, BinaryContentStatus.FAIL);
    }
}
