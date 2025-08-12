package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private final Instant createdAt;

    private UUID userId;
    private UUID channelId;
    private Instant lastReadAt;
    private Instant updatedAt;

    //final 생성자 초기화
    public ReadStatus(UUID id, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public ReadStatus(UUID id, UUID channelId, UUID userId, Instant createdAt) {
        this.id = id;
        this.channelId = channelId;
        this.userId = userId;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
        this.lastReadAt = createdAt;
    }

    public void update(Instant newLastReadAt) {

    }
}
