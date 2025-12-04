package com.sprint.mission.discodeit.infra.event.kafka;

import com.sprint.mission.discodeit.domain.entity.AuthAuditEventType;
import com.sprint.mission.discodeit.domain.entity.AuthAuditLog;
import com.sprint.mission.discodeit.domain.repository.AuthAuditLogRepository;
import com.sprint.mission.discodeit.infra.event.auth.RoleUpdatedEvent;
import com.sprint.mission.discodeit.infra.event.auth.TokenRefreshFailureEvent;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static org.springframework.util.StringUtils.hasText;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogEventConsumer {

    private static final String X_FORWARDED_FOR = "X-Forwarded-For";
    private static final String USER_AGENT = "User-Agent";
    private static final int USER_AGENT_MAX_LENGTH = 500;
    private final AuthAuditLogRepository authAuditLogRepository;

    public void logLoginSuccess(UUID userId, String username, HttpServletRequest request) {
        AuthAuditEvent event = new AuthAuditEvent(
            AuthAuditEventType.LOGIN_SUCCESS,
            userId,
            username,
            extractIpAddress(request),
            extractUserAgent(request),
            null
        );
        publishEvent(event);
        log.debug("Login success event published for user: {}", username);
    }

    public void logLoginFailure(String username, HttpServletRequest request, String reason) {
        AuthAuditEvent event = new AuthAuditEvent(
            AuthAuditEventType.LOGIN_FAILURE,
            null,
            username,
            extractIpAddress(request),
            extractUserAgent(request),
            reason
        );
        publishEvent(event);
        log.debug("Login failure event published for user: {}", username);
    }

    public void logLogout(UUID userId, String username, HttpServletRequest request) {
        AuthAuditEvent event = new AuthAuditEvent(
            AuthAuditEventType.LOGOUT,
            userId,
            username,
            extractIpAddress(request),
            extractUserAgent(request),
            null
        );
        publishEvent(event);
        log.debug("Logout event published for user: {}", username);
    }

    public void logTokenRefresh(UUID userId, String username, HttpServletRequest request) {
        AuthAuditEvent event = new AuthAuditEvent(
            AuthAuditEventType.TOKEN_REFRESH,
            userId,
            username,
            extractIpAddress(request),
            extractUserAgent(request),
            null
        );
        publishEvent(event);
        log.debug("Token refresh event published for user: {}", username);
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

    private String extractIpAddress(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String xForwardedFor = request.getHeader(X_FORWARDED_FOR);
        if (hasText(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String extractUserAgent(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String userAgent = request.getHeader(USER_AGENT);
        if (userAgent != null && userAgent.length() > USER_AGENT_MAX_LENGTH) {
            return userAgent.substring(0, USER_AGENT_MAX_LENGTH);
        }
        return userAgent;
    }
}
