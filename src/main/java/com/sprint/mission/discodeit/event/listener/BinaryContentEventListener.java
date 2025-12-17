package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.message.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class BinaryContentEventListener {

    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentService binaryContentService;

    // 별도 스레드에서 비동기 실행
    @Async("eventTaskExecutor")
    // Event 발행 트랜잭션이 정상 커밋된 후 실행
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBinaryContentCreated(BinaryContentCreatedEvent event) {
        BinaryContent binaryContent = event.getData();

        try {
            // 실제 바이너리 데이터 저장
            binaryContentStorage.put(binaryContent.getId(), event.getBytes());
            // 성공 시 상태를 "SUCCESS"로 변경
            binaryContentService.updateStatus(binaryContent.getId(), BinaryContentStatus.SUCCESS);
        } catch (RuntimeException e) {
            // 실패 시 상태를 "FAIL"로 변경
            binaryContentService.updateStatus(binaryContent.getId(), BinaryContentStatus.FAIL);
        }
    }
}
