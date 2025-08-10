package com.sprint.mission.discodeit.service.dto.userstatus;

import java.time.Instant;
import java.util.UUID;

/** 생성 DTO */
public class UserStatusCreateRequest {

    private UUID userId;          // 필수
    private boolean online;       // 필수
    private Instant lastSeenAt;   // 선택(없으면 now()로 저장하는 쪽 권장)

    public UserStatusCreateRequest() {}

    public UserStatusCreateRequest(UUID userId, boolean online, Instant lastSeenAt) {
        this.userId = userId;
        this.online = online;
        this.lastSeenAt = lastSeenAt;
    }

    public UUID getUserId() { return userId; }
    public boolean isOnline() { return online; }
    public Instant getLastSeenAt() { return lastSeenAt; }
}