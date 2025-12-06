package com.sprint.mission.discodeit.infrastructure.messaging.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.auth.domain.AuthAuditLog;
import com.sprint.mission.discodeit.auth.domain.AuthAuditLogRepository;
import com.sprint.mission.discodeit.auth.domain.CredentialUpdated;
import com.sprint.mission.discodeit.auth.domain.LoginEvent;
import com.sprint.mission.discodeit.auth.domain.LogoutEvent;
import com.sprint.mission.discodeit.auth.domain.RoleUpdatedEvent;
import com.sprint.mission.discodeit.auth.domain.TokenRefreshEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLogKafkaSubscriber {

    private final AuthAuditLogRepository authAuditLogRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = LoginEvent.TOPIC)
    public void logLogin(String payload) {
        try {
            LoginEvent event = objectMapper.readValue(payload, LoginEvent.class);

            AuthAuditLog auditLog = AuthAuditLog.login(
                event.userId(),
                event.username(),
                event.ipAddress(),
                event.userAgent()
            );

            authAuditLogRepository.save(auditLog);
        } catch (JsonProcessingException e) {
            log.error("로그인 이벤트 파싱 실패: payload={}", payload, e);
        }
    }

    @KafkaListener(topics = LogoutEvent.TOPIC)
    public void logLogout(LogoutEvent event) {
        AuthAuditLog auditLog = AuthAuditLog.logout(
            event.userId(),
            event.username(),
            event.ipAddress(),
            event.userAgent()
        );
        authAuditLogRepository.save(auditLog);
    }

    @KafkaListener(topics = TokenRefreshEvent.TOPIC)
    public void logTokenRefresh(TokenRefreshEvent event) {
        AuthAuditLog auditLog = AuthAuditLog.tokenRefresh(
            event.userId(),
            event.username(),
            event.ipAddress(),
            event.userAgent()
        );
        authAuditLogRepository.save(auditLog);
    }

    @KafkaListener(topics = RoleUpdatedEvent.TOPIC)
    public void logRoleUpdated(RoleUpdatedEvent event) {
        AuthAuditLog auditLog = AuthAuditLog.roleUpdated(
            event.userId(),
            event.username(),
            event.oldRole(),
            event.newRole()
        );
        authAuditLogRepository.save(auditLog);
    }

    @KafkaListener(topics = CredentialUpdated.TOPIC)
    public void logCredentialUpdated(CredentialUpdated event) {
        AuthAuditLog auditLog = AuthAuditLog.credentialUpdated(
            event.userId(),
            event.username(),
            event.ipAddress(),
            event.userAgent()
        );
        authAuditLogRepository.save(auditLog);
    }
}
