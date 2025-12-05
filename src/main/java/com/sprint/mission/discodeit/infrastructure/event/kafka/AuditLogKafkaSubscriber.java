package com.sprint.mission.discodeit.infrastructure.event.kafka;

import com.sprint.mission.discodeit.domain.auth.entity.AuthAuditLog;
import com.sprint.mission.discodeit.domain.auth.event.LoginFailureEvent;
import com.sprint.mission.discodeit.domain.auth.event.LoginSuccessEvent;
import com.sprint.mission.discodeit.domain.auth.event.LogoutEvent;
import com.sprint.mission.discodeit.domain.auth.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.domain.auth.event.TokenRefreshFailureEvent;
import com.sprint.mission.discodeit.domain.auth.event.TokenRefreshSuccessEvent;
import com.sprint.mission.discodeit.domain.auth.repository.AuthAuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLogKafkaSubscriber {

    private final AuthAuditLogRepository authAuditLogRepository;

    @KafkaListener(topics = "discodeit.LoginSuccessEvent")
    public void logLoginSuccess(LoginSuccessEvent event) {
        AuthAuditLog auditLog = AuthAuditLog.logout(
            event.userId(),
            event.username(),
            event.ipAddress(),
            event.userAgent()
        );
        authAuditLogRepository.save(auditLog);
    }

    @KafkaListener(topics = "discodeit.LoginFailureEvent")
    public void logLoginFailure(LoginFailureEvent event) {
        AuthAuditLog auditLog = AuthAuditLog.loginFailure(
            event.username(),
            event.ipAddress(),
            event.userAgent(),
            event.reason()
        );
        authAuditLogRepository.save(auditLog);
    }

    @KafkaListener(topics = "discodeit.LogoutEvent")
    public void logLogout(LogoutEvent event) {
        AuthAuditLog auditLog = AuthAuditLog.logout(
            event.userId(),
            event.username(),
            event.ipAddress(),
            event.userAgent()
        );
        authAuditLogRepository.save(auditLog);
    }

    @KafkaListener(topics = "discodeit.TokenRefreshSuccessEvent")
    public void logTokenRefresh(TokenRefreshSuccessEvent event) {
        AuthAuditLog auditLog = AuthAuditLog.tokenRefresh(
            event.userId(),
            event.username(),
            event.ipAddress(),
            event.userAgent()
        );
        authAuditLogRepository.save(auditLog);
    }

    @KafkaListener(topics = "discodeit.TokenRefreshFailureEvent")
    public void logTokenRefreshFailure(TokenRefreshFailureEvent event) {
        AuthAuditLog auditLog = AuthAuditLog.tokenRefreshFailure(
            event.userId(),
            event.username(),
            event.ipAddress(),
            event.userAgent(),
            event.reason()
        );
        authAuditLogRepository.save(auditLog);
    }

    @KafkaListener(topics = "discodeit.RoleUpdatedEvent")
    public void logRoleUpdated(RoleUpdatedEvent event) {
        AuthAuditLog auditLog = AuthAuditLog.roleUpdated(
            event.userId(),
            event.username(),
            event.oldRole(),
            event.newRole()
        );
        authAuditLogRepository.save(auditLog);
    }
}
