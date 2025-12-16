package com.sprint.mission.discodeit.linstener;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BinaryContentEventListener {

    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentService binaryContentService;

    @Async(value = "eventExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(BinaryContentCreatedEvent event) {

        UUID id = event.getBinaryContentId();
        byte[] bytes = event.getBytes();

        try {
            // 실제 바이너리 파일 저장
            binaryContentStorage.put(id, bytes);

            // 성공 상태 반영
            binaryContentService.updateStatus(id, BinaryContentStatus.SUCCESS);

        } catch (Exception e) {
            log.error("파일 저장 실패: id={}, error={}", id, e.getMessage());
            binaryContentService.updateStatus(id, BinaryContentStatus.FAIL);
        }
    }
}
