package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Channel {
    private UUID id;
    private Long createdAt;
    private Long updatedAt;

    private String name;

    public Channel(String name) {
        id = UUID.randomUUID();
        this.name = name;
        createdAt = System.currentTimeMillis();
    }

    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public String getName() {
        return name;
    }

    public void update(String name) {
        this.name = name;
        updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", name='" + name + '\'' +
                '}';
    }
}
