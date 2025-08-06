package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus {

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;

    private Instant lastActive;

    public UserStatus(User user) {
        super();
        this.id = user.getId();
        this.createdAt = Instant.now();
        this.lastActive = Instant.now();
    }

    // 접속 시간 갱신
    public void updateLastActive() {
        this.lastActive = Instant.now();
        this.updatedAt = Instant.now();
    }

    // 접속 중인 사용자 판단
    public boolean isActive() {
        if(lastActive == null) {
            return false;
        }

        Instant now = Instant.now().minusSeconds(300);
        return lastActive.isAfter(now);
    }
}
