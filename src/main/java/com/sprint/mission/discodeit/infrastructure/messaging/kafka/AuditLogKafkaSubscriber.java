package com.sprint.mission.discodeit.infrastructure.messaging.kafka;

import com.sprint.mission.discodeit.domain.auth.domain.AuthAuditLog;
import com.sprint.mission.discodeit.domain.auth.domain.AuthAuditLogRepository;
import com.sprint.mission.discodeit.domain.auth.domain.event.LoginFailureEvent;
import com.sprint.mission.discodeit.domain.auth.domain.event.LoginSuccessEvent;
import com.sprint.mission.discodeit.domain.auth.domain.event.LogoutEvent;
import com.sprint.mission.discodeit.domain.auth.domain.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.domain.auth.domain.event.TokenRefreshFailureEvent;
import com.sprint.mission.discodeit.domain.auth.domain.event.TokenRefreshSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLogKafkaSubscriber {

    private final AuthAuditLogRepository authAuditLogRepository;

    @KafkaListener(topics = LoginSuccessEvent.TOPIC)
    public void logLoginSuccess(LoginSuccessEvent event) {
        AuthAuditLog auditLog = AuthAuditLog.logout(
            event.userId(),
            event.username(),
            event.ipAddress(),
            event.userAgent()
        );
        authAuditLogRepository.save(auditLog);
    }

    @KafkaListener(topics = LoginFailureEvent.TOPIC)
    public void logLoginFailure(LoginFailureEvent event) {
        AuthAuditLog auditLog = AuthAuditLog.loginFailure(
            event.username(),
            event.ipAddress(),
            event.userAgent(),
            event.reason()
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

    @KafkaListener(topics = TokenRefreshSuccessEvent.TOPIC)
    public void logTokenRefresh(TokenRefreshSuccessEvent event) {
        AuthAuditLog auditLog = AuthAuditLog.tokenRefresh(
            event.userId(),
            event.username(),
            event.ipAddress(),
            event.userAgent()
        );
        authAuditLogRepository.save(auditLog);
    }

    @KafkaListener(topics = TokenRefreshFailureEvent.TOPIC)
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
}
