package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class User {
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    private String name;

    public User(String name) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
        this.name = name;
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
