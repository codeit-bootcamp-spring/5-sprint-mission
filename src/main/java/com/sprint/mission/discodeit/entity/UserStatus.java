package com.sprint.mission.discodeit.entity;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;


@Data
//유저상태
public class UserStatus implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;
    private UUID userId;
    private Instant updatedAt;
    private Instant lastOnline; // 마지막 접속시간

    public UserStatus(UUID id, Instant createdAt, UUID userId, Instant updatedAt, Instant lastOnline) {
        this.id = id;
        this.createdAt = createdAt;
        this.userId = userId;
        this.updatedAt = updatedAt;
        this.lastOnline = lastOnline;
    }

    public UserStatus(UUID id, Instant createdAt, UUID userId, Instant lastOnline) {
        this.id = id;
        this.createdAt = createdAt;
        this.userId = userId;
        this.lastOnline = lastOnline;
    }

    public boolean isOnline() {
        // 5분 이내 접속이면 온라인
        return lastOnline.isAfter(Instant.now().minusSeconds(300));
    }
}


