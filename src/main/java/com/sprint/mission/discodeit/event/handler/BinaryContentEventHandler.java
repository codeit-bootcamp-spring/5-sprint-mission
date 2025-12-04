package com.sprint.mission.discodeit.event.handler;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class BinaryContentEventHandler {
	private final BinaryContentStorage binaryContentStorage;

	@Async("taskExecutor")
	// @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleAfterCommitCreate(BinaryContentCreatedEvent event) {
		log.info("[AFTER_COMMIT] 파일 메타데이터 생성 커밋 완료: {}", event.binaryContentId());
		binaryContentStorage.put(event.binaryContentId(), event.file());
	}
}
