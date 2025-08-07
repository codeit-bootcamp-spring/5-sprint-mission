package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
@ToString
public class ReadStatus {

    private final UUID id;
    private final UUID channelId;
    private final UUID userId;
    private final Instant createdAt;
    private Instant updatedAt;

    public ReadStatus(UUID id, UUID channelId, UUID userId, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.channelId = channelId;
        this.userId = userId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}