package com.sprint.mission.discodeit.service.dto.userstatus;

import java.time.Instant;
import java.util.UUID;

/** 응답 DTO */
public class UserStatusResponse {

    private UUID id;
    private UUID userId;
    private boolean online;
    private Instant lastSeenAt;

    public UserStatusResponse(UUID id, UUID userId, boolean online, Instant lastSeenAt) {
        this.id = id;
        this.userId = userId;
        this.online = online;
        this.lastSeenAt = lastSeenAt;
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public boolean isOnline() { return online; }
    public Instant getLastSeenAt() { return lastSeenAt; }
}
