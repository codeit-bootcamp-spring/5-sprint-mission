package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.entity.AuthAuditLog;
import com.sprint.mission.discodeit.event.auth.AuthAuditEvent;
import com.sprint.mission.discodeit.repository.AuthAuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthAuditConsumer {

    private final AuthAuditLogRepository authAuditLogRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "discodeit.AuthAuditEvent")
    public void onAuthAuditEvent(String kafkaEvent) {
        try {
            AuthAuditEvent event = objectMapper.readValue(kafkaEvent, AuthAuditEvent.class);

            AuthAuditLog auditLog = AuthAuditLog.of(
                event.eventType(),
                event.userId(),
                event.username(),
                event.ipAddress(),
                event.userAgent(),
                event.details()
            );

            authAuditLogRepository.save(auditLog);
            log.debug("Auth audit log saved: eventType={}, username={}",
                event.eventType(), event.username());
        } catch (JsonProcessingException e) {
            log.error("AuthAuditEvent 역직렬화 실패: {}", kafkaEvent, e);
        } catch (Exception e) {
            log.error("Auth audit log 저장 실패: {}", e.getMessage(), e);
        }
    }
}
