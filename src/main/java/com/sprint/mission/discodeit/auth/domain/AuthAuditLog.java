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

    public static final int EVENT_TYPE_MAX_LENGTH = 30;
    public static final int USERNAME_MAX_LENGTH = 50;
    public static final int IP_ADDRESS_MAX_LENGTH = 45;
    public static final int USER_AGENT_MAX_LENGTH = 500;
    public static final int DETAILS_MAX_LENGTH = 500;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = EVENT_TYPE_MAX_LENGTH)
    private AuthAuditEventType eventType;

    @Column
    private UUID userId;

    @Column(length = USERNAME_MAX_LENGTH)
    private String username;

    @Column(length = IP_ADDRESS_MAX_LENGTH)
    private String ipAddress;

    @Column(length = USER_AGENT_MAX_LENGTH)
    private String userAgent;

    @Column(length = DETAILS_MAX_LENGTH)
    private String details;

    public static AuthAuditLog of(
        AuthAuditEventType eventType,
        UUID userId,
        String username,
        String ipAddress,
        String userAgent,
        String details
    ) {
        if (eventType == null) {
            throw new IllegalArgumentException("Event type cannot be null");
        }
        if (username != null && username.length() > USERNAME_MAX_LENGTH) {
            throw new IllegalArgumentException("Username exceeds maximum length");
        }
        if (ipAddress != null && ipAddress.length() > IP_ADDRESS_MAX_LENGTH) {
            throw new IllegalArgumentException("IP address exceeds maximum length");
        }
        if (userAgent != null && userAgent.length() > USER_AGENT_MAX_LENGTH) {
            throw new IllegalArgumentException("User agent exceeds maximum length");
        }
        if (details != null && details.length() > DETAILS_MAX_LENGTH) {
            throw new IllegalArgumentException("Details exceed maximum length");
        }

        return new AuthAuditLog(
            eventType,
            userId,
            username,
            ipAddress,
            userAgent,
            details
        );
    }

    public static AuthAuditLog login(
        UUID userId,
        String username,
        String ipAddress,
        String userAgent
    ) {
        return of(
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
        return of(
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
        return of(
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
        return of(
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
        return of(
            CREDENTIAL_UPDATED,
            userId,
            username,
            ipAddress,
            userAgent,
            null
        );
    }
}
