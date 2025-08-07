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
    private final Instant createdAt;
    private Instant updatedAt; // 마지막 접속 시간

    private boolean isLoggedIn; // 현재 상태(과연 이게 필요한가)
    private final UUID userId;

    public UserStatus(UUID userId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.isLoggedIn = true;
        this.userId = userId;
    }

    public void update() {
        this.updatedAt = Instant.now();
    }
}
