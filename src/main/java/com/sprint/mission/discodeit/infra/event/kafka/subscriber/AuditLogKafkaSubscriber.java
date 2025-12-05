package com.sprint.mission.discodeit.infra.event.kafka.subscriber;

import com.sprint.mission.discodeit.domain.entity.AuthAuditLog;
import com.sprint.mission.discodeit.domain.repository.AuthAuditLogRepository;
import com.sprint.mission.discodeit.infra.event.auth.LoginFailureEvent;
import com.sprint.mission.discodeit.infra.event.auth.LoginSuccessEvent;
import com.sprint.mission.discodeit.infra.event.auth.LogoutEvent;
import com.sprint.mission.discodeit.infra.event.auth.RoleUpdatedEvent;
import com.sprint.mission.discodeit.infra.event.auth.TokenRefreshFailureEvent;
import com.sprint.mission.discodeit.infra.event.auth.TokenRefreshSuccessEvent;
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

    @KafkaListener(topics = "discodeit.TokenRefreshFailedEvent")
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
    public void logRoleChange(RoleUpdatedEvent event) {
        AuthAuditLog auditLog = AuthAuditLog.roleChange(
            event.userId(),
            event.username(),
            event.oldRole(),
            event.newRole()
        );
        authAuditLogRepository.save(auditLog);
    }
}
