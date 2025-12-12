package com.sprint.mission.discodeit.integration;

import com.sprint.mission.discodeit.binarycontent.application.BinaryContentService;
import com.sprint.mission.discodeit.binarycontent.application.BinaryContentStorageProcessor;
import com.sprint.mission.discodeit.binarycontent.domain.BinaryContentStatus;
import com.sprint.mission.discodeit.binarycontent.domain.BinaryContentStorage;
import com.sprint.mission.discodeit.binarycontent.domain.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;

@SpringBootTest(classes = {BinaryContentStorageProcessor.class})
@EnableRetry
@TestPropertySource(properties = {
    "discodeit.storage.retry.max-attempts=3",
    "discodeit.storage.retry.backoff-delay=100",
    "discodeit.storage.retry.backoff-multiplier=1.0"
})
@DisplayName("BinaryContentStorageProcessor 통합 테스트")
class BinaryContentStorageProcessorIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private BinaryContentStorageProcessor processor;

    @Autowired
    private BinaryContentStorage storage;

    @Autowired
    private BinaryContentService service;

    @Test
    @DisplayName("실패 시 설정한 횟수만큼 재시도하고 recover로 넘어가는지 검증")
    void verifyRetryAndRecoverFlow() {
        // given
        UUID contentId = UUID.randomUUID();
        BinaryContentCreatedEvent event = new BinaryContentCreatedEvent(contentId, "test".getBytes());

        willThrow(new RuntimeException("Storage failure")).given(storage).put(any(), any());

        // when
        processor.processWithRetry(event);

        // then
        then(storage).should(times(3)).put(any(), any());
        then(service).should().updateStatus(contentId, BinaryContentStatus.FAIL);
    }

    @Test
    @DisplayName("성공 시 재시도 없이 SUCCESS 상태로 업데이트")
    void processWithRetry_onSuccess_updatesStatusToSuccess() {
        // given
        UUID contentId = UUID.randomUUID();
        BinaryContentCreatedEvent event = new BinaryContentCreatedEvent(contentId, "test".getBytes());

        // when
        processor.processWithRetry(event);

        // then
        then(storage).should(times(1)).put(contentId, event.bytes());
        then(service).should().updateStatus(contentId, BinaryContentStatus.SUCCESS);
    }
}
