package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public class UserStatus implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final UUID userId;
    private final Instant createdAt;
    private Instant updatedAt;
    private boolean loginStatus;

    public UserStatus(UUID userId) {
        id = UUID.randomUUID();
        createdAt = Instant.now();
        loginStatus = false;

        this.userId = userId;
    }

    public void update(boolean loginStatus) {
        boolean anyValueUpdated = false;

        if(this.loginStatus != loginStatus) {
            this.loginStatus = loginStatus;
            anyValueUpdated = true;
        }

        if(anyValueUpdated) {
            updatedAt = Instant.now();
        }
    }

    public boolean isLogin() {
        if (updatedAt != null && updatedAt.getEpochSecond() - Instant.now().getEpochSecond() <= 300) {
        loginStatus = true;
        }
        return loginStatus;
    }
}
