package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.UUID;

public class UserStatus {
    public final UUID id;
    private final Instant createdAt;
    public String userId;
    private Instant updatedAt; // 마지막 접속시간

    //final 생성자 초기화
    public UserStatus(UUID id, Instant createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }
}
