package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public class Channel implements Serializable {

    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final long createdAt;
    private final long updatedAt;
    private final String name;
    private final String description;
    private final String channelType;
    private final boolean isPrivate;

    public Channel(String name, String description, String channelType, boolean isPrivate) {
        this(UUID.randomUUID(), System.currentTimeMillis(), System.currentTimeMillis(), name, description, channelType, isPrivate);
    }

    public Channel(UUID id, long createdAt, long updatedAt, String name, String description, String channelType, boolean isPrivate) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.name = name;
        this.description = description;
        this.channelType = channelType;
        this.isPrivate = isPrivate;
    }

    public Channel(Channel other) {
        this(other.id, other.createdAt, other.updatedAt, other.name, other.description, other.channelType, other.isPrivate);
    }

    public Channel withName(String newName) {
        return new Channel(id, createdAt, System.currentTimeMillis(), newName, description, channelType, isPrivate);
    }

    public Channel withDescription(String newDescription) {
        return new Channel(id, createdAt, System.currentTimeMillis(), name, newDescription, channelType, isPrivate);
    }

    public Channel withChannelType(String newType) {
        return new Channel(id, createdAt, System.currentTimeMillis(), name, description, newType, isPrivate);
    }

    public Channel withPrivacy(boolean newPrivacy) {
        return new Channel(id, createdAt, System.currentTimeMillis(), name, description, channelType, newPrivacy);
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

    public String getDescription() {
        return description;
    }

    public String getChannelType() {
        return channelType;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", channelType='" + channelType + '\'' +
                ", isPrivate=" + isPrivate +
                '}';
    }
}


