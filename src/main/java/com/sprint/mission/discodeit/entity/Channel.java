package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Channel {
    private final UUID id;
    private final long createdAt;
    private final long updatedAt;
    private final String name;

    public Channel(String name) {
        this(UUID.randomUUID(), System.currentTimeMillis(), System.currentTimeMillis(), name);
    }

    public Channel(UUID id, long createdAt, long updatedAt, String name) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.name = name;
    }

    // 복사 생성자
    public Channel(Channel other) {
        this(other.id, other.createdAt, other.updatedAt, other.name);
    }

    public UUID getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public String getName() {
        return name;
    }
}

