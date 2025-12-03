package com.sprint.mission.discodeit.security.jwt;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(name = "jwt_sessions")
@NoArgsConstructor
public class JwtSession extends BaseUpdatableEntity {

    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID userId;

    @Column(columnDefinition = "varchar(512)", nullable = false, unique = true)
    private String accessToken;

    @Column(columnDefinition = "varchar(512)", nullable = false, unique = true)
    private String refreshToken;

    @Column(columnDefinition = "timestamp with time zone", nullable = false)
    private Instant expirationTime;

    public JwtSession(UUID userId, String accessToken, String refreshToken, Instant expirationTime) {
        this.userId = userId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expirationTime = expirationTime;
    }

    public void update(String accessToken, String refreshToken, Instant expirationTime) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expirationTime = expirationTime;
    }

    public boolean isExpired() {
        return this.expirationTime.isBefore(Instant.now());
    }
}
