package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class Channel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;

    private ChannelType type;
    private String name; // 채널명
    private UUID ownerId; // 채널 소유자

    public Channel(ChannelType type, String name, UUID ownerId) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.type = type;
        this.name = name;
        this.ownerId = ownerId;
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

    public ChannelType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void update(String name, UUID ownerId) {
        this.updatedAt = System.currentTimeMillis();
        this.name = name;
        this.ownerId = ownerId;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", ownerId=" + ownerId +
                '}';
    }
}
