package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.dto.BinaryContentDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.event.BinaryContentUpdatedEvent;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class BinaryContentSavedListener {

    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentRepository binaryContentRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Async("eventTaskExecutor")
    // 메타데이터 저장 커밋 이후
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    // 바이너리 파일 저장에 대한 트랜잭션
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleBinaryContentCreated(BinaryContentCreatedEvent event) {
        log.info("[BinaryContentSavedListener] 바이너리 파일 저장 시작: {}", event.getBinaryContentId());

        try {
            binaryContentStorage.put(event.getBinaryContentId(), event.getBytes());

            BinaryContent binaryContent = binaryContentRepository.findById(event.getBinaryContentId())
                    .orElseThrow(() -> BinaryContentNotFoundException.withBinaryContentId(event.getBinaryContentId()));
            binaryContent.updateStatus(BinaryContentStatus.SUCCESS);
            binaryContentRepository.save(binaryContent);

            log.info("[BinaryContentSavedListener] 바이너리 파일 저장 성공: {}", event.getBinaryContentId());

            if (event.getReceiverId() != null) {
                publishBinaryContentUpdatedEvent(binaryContent, event.getReceiverId());
            }

        } catch (Exception e) {
            log.error("[BinaryContentSavedListener] 바이너리 파일 저장 실패: {}", event.getBinaryContentId(), e);

            BinaryContent binaryContent = binaryContentRepository.findById(event.getBinaryContentId())
                    .orElseThrow(() -> BinaryContentNotFoundException.withBinaryContentId(event.getBinaryContentId()));
            binaryContent.updateStatus(BinaryContentStatus.FAIL);
            log.info("[BinaryContentSavedListener] 바이너리 컨텐츠 상태 : {}, id : {}", binaryContent.getStatus(), binaryContent.getId());
            binaryContentRepository.save(binaryContent);

            if (event.getReceiverId() != null) {
                publishBinaryContentUpdatedEvent(binaryContent, event.getReceiverId());
            }
        }
    }

    private void publishBinaryContentUpdatedEvent(BinaryContent binaryContent, UUID receiverId) {
        BinaryContentDTO dto = BinaryContentDTO.builder()
                .id(binaryContent.getId())
                .contentType(binaryContent.getContentType())
                .fileName(binaryContent.getFileName())
                .size(binaryContent.getSize())
                .status(binaryContent.getStatus())
                .build();

        eventPublisher.publishEvent(
                new BinaryContentUpdatedEvent(receiverId, dto)
        );

        log.debug("[BinaryContentSavedListener] BinaryContentUpdatedEvent 발행 - id: {}, receiverId: {}, status: {}",
                binaryContent.getId(), receiverId, binaryContent.getStatus());
    }
}