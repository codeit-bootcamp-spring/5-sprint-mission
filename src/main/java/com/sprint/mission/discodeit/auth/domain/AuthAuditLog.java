package com.sprint.mission.discodeit.auth.domain;

import com.sprint.mission.discodeit.common.domain.BaseEntity;
import com.sprint.mission.discodeit.user.domain.Role;
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

import static com.sprint.mission.discodeit.auth.domain.AuthAuditEventType.CREDENTIAL_UPDATED;
import static com.sprint.mission.discodeit.auth.domain.AuthAuditEventType.LOGIN_SUCCESS;
import static com.sprint.mission.discodeit.auth.domain.AuthAuditEventType.LOGOUT;
import static com.sprint.mission.discodeit.auth.domain.AuthAuditEventType.ROLE_UPDATED;
import static com.sprint.mission.discodeit.auth.domain.AuthAuditEventType.TOKEN_REFRESH;

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

    public static AuthAuditLog login(
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

    public static AuthAuditLog credentialUpdated(
        UUID userId,
        String username,
        String ipAddress,
        String userAgent
    ) {
        return new AuthAuditLog(
            CREDENTIAL_UPDATED,
            userId,
            username,
            ipAddress,
            userAgent,
            null
        );
    }
}
