package com.sprint.mission.discodeit.domain.auth.entity;

import com.sprint.mission.discodeit.domain.common.entity.BaseEntity;
import com.sprint.mission.discodeit.domain.user.entity.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static com.sprint.mission.discodeit.domain.auth.entity.AuthAuditEventType.LOGIN_FAILURE;
import static com.sprint.mission.discodeit.domain.auth.entity.AuthAuditEventType.LOGIN_SUCCESS;
import static com.sprint.mission.discodeit.domain.auth.entity.AuthAuditEventType.LOGOUT;
import static com.sprint.mission.discodeit.domain.auth.entity.AuthAuditEventType.ROLE_UPDATED;
import static com.sprint.mission.discodeit.domain.auth.entity.AuthAuditEventType.TOKEN_REFRESH;
import static com.sprint.mission.discodeit.domain.auth.entity.AuthAuditEventType.TOKEN_REFRESH_FAILURE;

@Entity
@Table(name = "auth_audit_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthAuditLog extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AuthAuditEventType eventType;

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

    public static AuthAuditLog of(
        AuthAuditEventType eventType,
        UUID userId,
        String username,
        String ipAddress,
        String userAgent,
        String details
    ) {
        return new AuthAuditLog(
            eventType,
            userId,
            username,
            ipAddress,
            userAgent,
            details
        );
    }

    public static AuthAuditLog loginSuccess(
        UUID userId,
        String username,
        String ipAddress,
        String userAgent
    ) {
        return new AuthAuditLog(
            LOGIN_SUCCESS,
            userId,
            username,
            ipAddress,
            userAgent,
            null
        );
    }

    public static AuthAuditLog loginFailure(
        String username,
        String ipAddress,
        String userAgent,
        String reason
    ) {
        return new AuthAuditLog(
            LOGIN_FAILURE,
            null,
            username,
            ipAddress,
            userAgent,
            reason
        );
    }

    public static AuthAuditLog logout(
        UUID userId,
        String username,
        String ipAddress,
        String userAgent
    ) {
        return new AuthAuditLog(
            LOGOUT,
            userId,
            username,
            ipAddress,
            userAgent,
            null
        );
    }

    public static AuthAuditLog tokenRefresh(
        UUID userId,
        String username,
        String ipAddress,
        String userAgent
    ) {
        return new AuthAuditLog(
            TOKEN_REFRESH,
            userId,
            username,
            ipAddress,
            userAgent,
            null
        );
    }

    public static AuthAuditLog tokenRefreshFailure(
        UUID userId,
        String username,
        String ipAddress,
        String userAgent,
        String reason
    ) {
        return new AuthAuditLog(
            TOKEN_REFRESH_FAILURE,
            userId,
            username,
            ipAddress,
            userAgent,
            reason
        );
    }

    public static AuthAuditLog roleUpdated(
        UUID userId,
        String username,
        Role oldRole,
        Role newRole
    ) {
        String details = "Role changed from %s to %s".formatted(oldRole, newRole);
        return new AuthAuditLog(
            ROLE_UPDATED,
            userId,
            username,
            null,
            null,
            details
        );
    }
}
