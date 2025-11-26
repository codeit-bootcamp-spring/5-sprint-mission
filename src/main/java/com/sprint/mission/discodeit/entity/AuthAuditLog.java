package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "auth_audit_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class AuthAuditLog extends BaseEntity {

    public enum EventType {
        LOGIN_SUCCESS,
        LOGIN_FAILURE,
        LOGOUT,
        TOKEN_REFRESH,
        TOKEN_REFRESH_FAILURE,
        ROLE_CHANGE
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EventType eventType;

    @Column
    private UUID userId;

    @Column(length = 50)
    private String username;

    @Column(length = 45)
    private String ipAddress;

    @Column(length = 500)
    private String userAgent;

    @Column(length = 500)
    private String details;

    private AuthAuditLog(EventType eventType, UUID userId, String username,
                         String ipAddress, String userAgent, String details) {
        this.eventType = eventType;
        this.userId = userId;
        this.username = username;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.details = details;
    }

    public static AuthAuditLog of(EventType eventType, UUID userId, String username,
                                  String ipAddress, String userAgent, String details) {
        return new AuthAuditLog(eventType, userId, username, ipAddress, userAgent, details);
    }

    public static AuthAuditLog loginSuccess(UUID userId, String username,
                                            String ipAddress, String userAgent) {
        return new AuthAuditLog(EventType.LOGIN_SUCCESS, userId, username,
            ipAddress, userAgent, null);
    }

    public static AuthAuditLog loginFailure(String username, String ipAddress,
                                            String userAgent, String reason) {
        return new AuthAuditLog(EventType.LOGIN_FAILURE, null, username,
            ipAddress, userAgent, reason);
    }

    public static AuthAuditLog logout(UUID userId, String username,
                                      String ipAddress, String userAgent) {
        return new AuthAuditLog(EventType.LOGOUT, userId, username,
            ipAddress, userAgent, null);
    }

    public static AuthAuditLog tokenRefresh(UUID userId, String username,
                                            String ipAddress, String userAgent) {
        return new AuthAuditLog(EventType.TOKEN_REFRESH, userId, username,
            ipAddress, userAgent, null);
    }

    public static AuthAuditLog tokenRefreshFailure(String ipAddress, String userAgent, String reason) {
        return new AuthAuditLog(EventType.TOKEN_REFRESH_FAILURE, null, null,
            ipAddress, userAgent, reason);
    }

    public static AuthAuditLog roleChange(UUID userId, String username, String oldRole, String newRole) {
        String details = "Role changed from %s to %s".formatted(oldRole, newRole);
        return new AuthAuditLog(EventType.ROLE_CHANGE, userId, username,
            null, null, details);
    }
}
