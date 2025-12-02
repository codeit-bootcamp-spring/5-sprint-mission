package com.sprint.mission.discodeit.security.audit;

import com.sprint.mission.discodeit.entity.AuthAuditLog;
import com.sprint.mission.discodeit.repository.AuthAuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthAuditService {

    private static final String X_FORWARDED_FOR = "X-Forwarded-For";
    private static final String USER_AGENT = "User-Agent";

    private final AuthAuditLogRepository authAuditLogRepository;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logLoginSuccess(UUID userId, String username, HttpServletRequest request) {
        AuthAuditLog auditLog = AuthAuditLog.loginSuccess(
            userId,
            username,
            extractIpAddress(request),
            extractUserAgent(request)
        );
        save(auditLog);
        log.debug("Login success recorded for user: {}", username);
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logLoginFailure(String username, HttpServletRequest request, String reason) {
        AuthAuditLog auditLog = AuthAuditLog.loginFailure(
            username,
            extractIpAddress(request),
            extractUserAgent(request),
            reason
        );
        save(auditLog);
        log.debug("Login failure recorded for user: {}", username);
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logLogout(UUID userId, String username, HttpServletRequest request) {
        AuthAuditLog auditLog = AuthAuditLog.logout(
            userId,
            username,
            extractIpAddress(request),
            extractUserAgent(request)
        );
        save(auditLog);
        log.debug("Logout recorded for user: {}", username);
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logTokenRefresh(UUID userId, String username, HttpServletRequest request) {
        AuthAuditLog auditLog = AuthAuditLog.tokenRefresh(
            userId,
            username,
            extractIpAddress(request),
            extractUserAgent(request)
        );
        save(auditLog);
        log.debug("Token refresh recorded for user: {}", username);
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logTokenRefreshFailure(HttpServletRequest request, String reason) {
        AuthAuditLog auditLog = AuthAuditLog.tokenRefreshFailure(
            extractIpAddress(request),
            extractUserAgent(request),
            reason
        );
        save(auditLog);
        log.debug("Token refresh failure recorded");
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logRoleChange(UUID userId, String username, String oldRole, String newRole) {
        AuthAuditLog auditLog = AuthAuditLog.roleChange(userId, username, oldRole, newRole);
        save(auditLog);
        log.debug("Role change recorded for user: {} ({} -> {})", username, oldRole, newRole);
    }

    private void save(AuthAuditLog auditLog) {
        try {
            authAuditLogRepository.save(auditLog);
        } catch (Exception e) {
            log.error("Failed to save auth audit log: {}", e.getMessage());
        }
    }

    private String extractIpAddress(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String xForwardedFor = request.getHeader(X_FORWARDED_FOR);
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String extractUserAgent(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String userAgent = request.getHeader(USER_AGENT);
        if (userAgent != null && userAgent.length() > 500) {
            return userAgent.substring(0, 500);
        }
        return userAgent;
    }
}
