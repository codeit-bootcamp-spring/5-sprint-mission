package com.sprint.mission.discodeit.event.audit;

import com.sprint.mission.discodeit.entity.AuthAuditEventType;
import com.sprint.mission.discodeit.event.auth.AuthAuditEvent;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static org.springframework.util.StringUtils.hasText;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthAuditPublisher {

    private static final String X_FORWARDED_FOR = "X-Forwarded-For";
    private static final String USER_AGENT = "User-Agent";
    private static final int USER_AGENT_MAX_LENGTH = 500;

    private final ApplicationEventPublisher eventPublisher;

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

    public void logTokenRefreshFailure(HttpServletRequest request, String reason) {
        AuthAuditEvent event = new AuthAuditEvent(
            AuthAuditEventType.TOKEN_REFRESH_FAILURE,
            null,
            null,
            extractIpAddress(request),
            extractUserAgent(request),
            reason
        );
        publishEvent(event);
        log.debug("Token refresh failure event published");
    }

    public void logRoleChange(UUID userId, String username, String oldRole, String newRole) {
        String details = "Role changed from %s to %s".formatted(oldRole, newRole);
        AuthAuditEvent event = new AuthAuditEvent(
            AuthAuditEventType.ROLE_CHANGE,
            userId,
            username,
            null,
            null,
            details
        );
        publishEvent(event);
        log.debug("Role change event published for user: {} ({} -> {})", username, oldRole, newRole);
    }

    private void publishEvent(AuthAuditEvent event) {
        eventPublisher.publishEvent(event);
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
