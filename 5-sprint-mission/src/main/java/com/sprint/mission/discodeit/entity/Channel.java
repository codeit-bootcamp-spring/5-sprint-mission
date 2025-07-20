package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Channel {
    private UUID id;
    private Long createdAt;
    private Long updatedAt;
    private String name;

    public Channel(String name) {
        this.name = name;
        this.createdAt = System.currentTimeMillis();
        this.id = UUID.randomUUID();
        this.updatedAt = System.currentTimeMillis();
    }

    public UUID getChId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public String getChName() {
        return name;
    }

    public void update(String name) {
        this.name = name;
        this.updatedAt = System.currentTimeMillis();
    }
}
