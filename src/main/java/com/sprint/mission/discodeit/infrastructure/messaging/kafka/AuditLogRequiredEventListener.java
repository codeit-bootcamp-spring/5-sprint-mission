package com.sprint.mission.discodeit.infrastructure.messaging.kafka;

import com.sprint.mission.discodeit.auth.domain.AuthAuditLog;
import com.sprint.mission.discodeit.auth.domain.AuthAuditLogRepository;
import com.sprint.mission.discodeit.auth.domain.event.CredentialUpdatedEvent;
import com.sprint.mission.discodeit.auth.domain.event.LoginEvent;
import com.sprint.mission.discodeit.auth.domain.event.LogoutEvent;
import com.sprint.mission.discodeit.auth.domain.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.auth.domain.event.TokenRefreshEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditLogRequiredEventListener {

    private final AuthAuditLogRepository authAuditLogRepository;

    @KafkaListener(topics = LoginEvent.TOPIC)
    public void logLogin(LoginEvent event) {
        AuthAuditLog auditLog = AuthAuditLog.login(
            event.userId(),
            event.username(),
            event.ipAddress(),
            event.userAgent()
        );
        authAuditLogRepository.save(auditLog);
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

    @KafkaListener(topics = CredentialUpdatedEvent.TOPIC)
    public void logCredentialUpdated(CredentialUpdatedEvent event) {
        AuthAuditLog auditLog = AuthAuditLog.credentialUpdated(
            event.userId(),
            event.username(),
            event.ipAddress(),
            event.userAgent()
        );
        authAuditLogRepository.save(auditLog);
    }
}
