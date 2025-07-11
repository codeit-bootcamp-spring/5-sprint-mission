package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class User {
    private UUID id;
    private String name;
    private Boolean isOnline;
    private Long createdAt;
    private Long updatedAt;

    public User() {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
    }

    public User(String name, boolean isOnline) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.name = name;
        this.isOnline = isOnline;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    private void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    private void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getOnline() {
        return isOnline;
    }

    private void setOnline(Boolean online) {
        isOnline = online;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    private void setId(UUID id) {
        this.id = id;
    }

    public void update(String name, boolean isOnline) {
        setName(name);
        setOnline(isOnline);
        setUpdatedAt(System.currentTimeMillis());
    }
}
