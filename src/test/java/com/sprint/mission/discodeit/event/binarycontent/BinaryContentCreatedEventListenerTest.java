package com.sprint.mission.discodeit.event.binarycontent;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class BinaryContentCreatedEventListenerTest {

    @Mock
    private BinaryContentStorage binaryContentStorage;

    @Mock
    private BinaryContentService binaryContentService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

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
    @DisplayName("storeWithRetry 메서드가 정상적으로 스토리지에 저장")
    void storeWithRetry_Success() {
        // given
        UUID binaryContentId = UUID.randomUUID();
        byte[] bytes = "test content".getBytes();

        // when
        listener.storeWithRetry(binaryContentId, bytes);

        // then
        then(binaryContentStorage).should().put(binaryContentId, bytes);
    }

    @Test
    @DisplayName("recover 메서드 호출 시 상태를 FAIL로 업데이트하고 BinaryContentUploadFailedEvent 발행 후 예외 발생")
    void recover_UpdatesStatusAndPublishesEvent() {
        // given
        UUID binaryContentId = UUID.randomUUID();
        byte[] bytes = "test content".getBytes();
        Exception exception = new RuntimeException("Storage error");
        String requestId = "test-request-id";

        MDC.put("requestId", requestId);

        try {
            // when & then
            assertThatThrownBy(() -> listener.recover(exception, binaryContentId, bytes))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("바이너리 콘텐츠 저장 실패");

            then(binaryContentService).should().updateStatus(binaryContentId, BinaryContentStatus.FAIL);

            ArgumentCaptor<BinaryContentUploadFailedEvent> eventCaptor =
                ArgumentCaptor.forClass(BinaryContentUploadFailedEvent.class);
            then(eventPublisher).should().publishEvent(eventCaptor.capture());

            BinaryContentUploadFailedEvent capturedEvent = eventCaptor.getValue();
            assertThat(capturedEvent.binaryContentId()).isEqualTo(binaryContentId);
            assertThat(capturedEvent.requestId()).isEqualTo(requestId);
            assertThat(capturedEvent.errorMessage()).isEqualTo("Storage error");
        } finally {
            MDC.clear();
        }
    }

    @Test
    @DisplayName("recover 시 requestId가 없어도 BinaryContentUploadFailedEvent 발행 후 예외 발생")
    void recover_WithoutRequestId_PublishesEvent() {
        // given
        UUID binaryContentId = UUID.randomUUID();
        byte[] bytes = "test content".getBytes();
        Exception exception = new RuntimeException("Storage error");

        MDC.clear();

        // when & then
        assertThatThrownBy(() -> listener.recover(exception, binaryContentId, bytes))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("바이너리 콘텐츠 저장 실패");

        then(binaryContentService).should().updateStatus(binaryContentId, BinaryContentStatus.FAIL);

        ArgumentCaptor<BinaryContentUploadFailedEvent> eventCaptor =
            ArgumentCaptor.forClass(BinaryContentUploadFailedEvent.class);
        then(eventPublisher).should().publishEvent(eventCaptor.capture());

        BinaryContentUploadFailedEvent capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.binaryContentId()).isEqualTo(binaryContentId);
        assertThat(capturedEvent.requestId()).isNull();
        assertThat(capturedEvent.errorMessage()).isEqualTo("Storage error");
    }
}
