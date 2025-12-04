package com.sprint.mission.discodeit.event.handler;

import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProduceRequiredEventListener {

	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper;

	@Async("taskExecutor")
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void on(MessageCreatedEvent event) throws JsonProcessingException {
		String payload = objectMapper.writeValueAsString(event);
		kafkaTemplate.send("discodeit.MessageCreatedEvent", payload)
			.whenComplete((r, e) -> {
				if (e != null) {
					log.warn("[Kafka] 메세지 생성 이벤트 전송 실패: reason={}", e.getMessage());
				} else {
					log.info("[Kafka] 메세지 생성 이벤트 전송 성공, topic={}, partition={}, offset={}",
						r.getRecordMetadata().topic(),
						r.getRecordMetadata().partition(),
						r.getRecordMetadata().offset());
				}
			});
	}

	@Async("taskExecutor")
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void on(RoleUpdatedEvent event) throws JsonProcessingException {
		String payload = objectMapper.writeValueAsString(event);
		kafkaTemplate.send("discodeit.RoleUpdatedEvent", payload)
			.whenComplete((r, e) -> {
				if (e != null) {
					log.warn("[Kafka] 권한 변경 이벤트 전송 실패: reason={}", e.getMessage());
				} else {
					log.info("[Kafka] 권한 변경 이벤트 전송 성공, topic={}, partition={}, offset={}",
						r.getRecordMetadata().topic(),
						r.getRecordMetadata().partition(),
						r.getRecordMetadata().offset());
				}
			});
	}

	@Async("taskExecutor")
	@EventListener
	public void on(S3UploadFailedEvent event) throws JsonProcessingException {
		String payload = objectMapper.writeValueAsString(event);
		kafkaTemplate.send("discodeit.S3UploadFailedEvent", payload)
			.whenComplete((r, e) -> {
				if (e != null) {
					log.warn("[Kafka] S3 업로드 실패 이벤트 전송 실패: reason={}", e.getMessage());
				} else {
					log.info("[Kafka] S3 업로드 실패 이벤트 전송 성공, topic={}, partition={}, offset={}",
						r.getRecordMetadata().topic(),
						r.getRecordMetadata().partition(),
						r.getRecordMetadata().offset());
				}
			});
	}
}
