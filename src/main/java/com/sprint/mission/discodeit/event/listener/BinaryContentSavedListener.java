package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class BinaryContentSavedListener {

    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentRepository binaryContentRepository;

    @Async("eventTaskExecutor")
    // 메타데이터 저장 커밋 이후
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    // 바이너리 파일 저장에 대한 트랜잭션
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleBinaryContentCreated(BinaryContentCreatedEvent event) {
        log.info("[Listener] 바이너리 파일 저장 시작: {}", event.getBinaryContentId());

        try {
            binaryContentStorage.put(event.getBinaryContentId(), event.getBytes());

            BinaryContent binaryContent = binaryContentRepository.findById(event.getBinaryContentId())
                    .orElseThrow(() -> BinaryContentNotFoundException.withBinaryContentId(event.getBinaryContentId()));
            binaryContent.updateStatus(BinaryContentStatus.SUCCESS);
            binaryContentRepository.save(binaryContent);

            log.info("[Listener] 바이너리 파일 저장 성공: {}", event.getBinaryContentId());

        } catch (Exception e) {
            log.error("[Listener] 바이너리 파일 저장 실패: {}", event.getBinaryContentId(), e);

            BinaryContent binaryContent = binaryContentRepository.findById(event.getBinaryContentId())
                    .orElseThrow(() -> BinaryContentNotFoundException.withBinaryContentId(event.getBinaryContentId()));
            binaryContent.updateStatus(BinaryContentStatus.FAIL);
            log.info("[Listener] 바이너리 컨텐츠 상태 : {}, id : {}", binaryContent.getStatus(), binaryContent.getId());
            binaryContentRepository.save(binaryContent);
        }
    }
}