package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class User {
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    private String username;

    public User(String username) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.username = username;
        this.updatedAt = this.createdAt;
    }

    public UUID getId() { return id; }
    public Long getCreatedAt() { return createdAt; }
    public Long getUpdatedAt() { return updatedAt; }
    public String getUsername() { return username; }

    public void update(String username) {
        this.username = username;
        this.updatedAt = System.currentTimeMillis();
    }
}
