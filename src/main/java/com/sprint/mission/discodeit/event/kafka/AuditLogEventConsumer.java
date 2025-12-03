package com.sprint.mission.discodeit.event.kafka;

import com.sprint.mission.discodeit.entity.AuthAuditLog;
import com.sprint.mission.discodeit.event.audit.AuthAuditPublisher;
import com.sprint.mission.discodeit.event.auth.AuthAuditEvent;
import com.sprint.mission.discodeit.event.auth.RoleUpdatedEvent;
import com.sprint.mission.discodeit.repository.AuthAuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuditLogEventConsumer {

    private final AuthAuditPublisher authAuditPublisher;

    private final AuthAuditLogRepository authAuditLogRepository;

    @KafkaListener(topics = "discodeit.RoleUpdatedEvent", groupId = "audit-group")
    public void onRoleUpdated(RoleUpdatedEvent event) {
        authAuditPublisher.logRoleChange(
            event.userId(),
            event.username(),
            event.oldRole().name(),
            event.newRole().name()
        );
        log.info("권한 변경 감사 로그 저장 완료: userId={}", event.userId());
    }

    @KafkaListener(topics = "discodeit.AuthAuditEvent")
    public void onAuthAuditEvent(AuthAuditEvent event) {
        AuthAuditLog auditLog = AuthAuditLog.of(
            event.eventType(),
            event.userId(),
            event.username(),
            event.ipAddress(),
            event.userAgent(),
            event.details()
        );

        authAuditLogRepository.save(auditLog);

        log.debug("인증 감사 로그 저장: eventType={}, username={}",
            event.eventType(), event.username());
    }
}
