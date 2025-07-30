package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Channel {
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    private String name;

    public Channel(String name) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.name = name;
        this.updatedAt = this.createdAt;
    }

    public UUID getId() { return id; }
    public Long getCreatedAt() { return createdAt; }
    public Long getUpdatedAt() { return updatedAt; }
    public String getName() { return name; }

    public void update(String name) {
        this.name = name;
        this.updatedAt = System.currentTimeMillis();
    }
}
