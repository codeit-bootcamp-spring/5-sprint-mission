package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Channel {
    private UUID id;
    private String name;
    private String description;
    private Long createdAt;
    private Long updatedAt;

    public Channel() {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
    }

    public Channel(String name, String description) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.name = name;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Channel{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", createdAt=" + createdAt +
            ", updatedAt=" + updatedAt +
            '}';
    }

    public UUID getId() {
        return id;
    }

    private void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    private void setDescription(String description) {
        this.description = description;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    private void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    private void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void update(String name, String description) {
        setName(name);
        setDescription(description);
        setUpdatedAt(System.currentTimeMillis());
    }
}
