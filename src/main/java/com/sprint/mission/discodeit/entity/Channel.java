package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public class Channel implements Serializable {
    private final UUID id;
    private String name;
    private String description;
    private ChannelType channelType;

    private long createdAt;
    private long updatedAt;

    public Channel(String name, String description, ChannelType channelType) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.channelType = channelType;
        this.createdAt = Instant.now().getEpochSecond();
        this.updatedAt = createdAt;
    }

    public long update() {
        return this.updatedAt = Instant.now().getEpochSecond();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ChannelType getChannelType() {
        return channelType;
    }

    public void setChannelType(ChannelType channelType) {
        this.channelType = channelType;
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
}
