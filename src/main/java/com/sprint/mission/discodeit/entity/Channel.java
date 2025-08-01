package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.enums.ChannelType;

import java.util.Objects;
import java.util.UUID;

import static java.time.Instant.*;

public class Channel {

    private final UUID id;
    private final Long createdAt;

    private Long updatedAt;
    private ChannelType type;
    private String name;
    private String description;

    public Channel(String name, String description, ChannelType type) {
        this.id = UUID.randomUUID();
        this.createdAt = now().getEpochSecond();

        this.name = name;
        this.description = description;
        this.type = type;
        this.updatedAt = now().getEpochSecond();
    }

    public UUID getId() { return id;}
    public Long getCreatedAt() { return createdAt; }
    public Long getUpdatedAt() { return updatedAt; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public ChannelType getType() { return type; }

    public void update(String name, String description, ChannelType type) {
        boolean anyValueUpdated = false;

        if(isNameChanged(name)) {
            this.name = name;
            anyValueUpdated = true;
        }

        if(isDescriptionChanged(description)) {
            this.description = description;
            anyValueUpdated = true;
        }

        if(isTypeChanged(type)) {
            this.type = type;
            anyValueUpdated = true;
        }

        if(anyValueUpdated) {
            this.updatedAt = now().getEpochSecond();
        }
    }

    public boolean isNameChanged(String name) {
        return name != null && Objects.equals(this.name, name);
    }

    public boolean isDescriptionChanged(String description) {
        return description != null && Objects.equals(this.description, description);
    }

    public boolean isTypeChanged(ChannelType type) {
        return type != null && Objects.equals(this.type, type);
    }
}
