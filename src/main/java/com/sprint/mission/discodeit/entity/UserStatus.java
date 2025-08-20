package com.sprint.mission.discodeit.entity;


import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;
    //
    private final UUID userId;
    private Instant lastLogin;

    public UserStatus(UUID userId, Instant lastLogin) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        //
        this.userId = userId;
        this.lastLogin = lastLogin;
    }

    public void updateLastLogin(Instant time) {
        this.updatedAt = time;
        this.lastLogin = time;
    }

    public boolean isLogin() {
        return lastLogin.isBefore(Instant.now().minus(Duration.ofMinutes(5)));
    }
}
