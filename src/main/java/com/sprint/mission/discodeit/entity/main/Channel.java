package com.sprint.mission.discodeit.entity.main;

import com.sprint.mission.discodeit.entity.enums.ChannelType;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import static java.time.Instant.*;

@Getter
public class Channel implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;

    private ChannelType type;
    private String name;
    private String description;

    private final Instant createdAt;
    private Instant updatedAt;

    public Channel(String name, String description, ChannelType type) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.type = type;
        this.createdAt = now();
        this.updatedAt = this.createdAt;
    }

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
            this.updatedAt = now();
        }
    }

    public boolean isNameChanged(String name) {
        return name != null && !Objects.equals(this.name, name);
    }

    public boolean isDescriptionChanged(String description) {
        return description != null && !Objects.equals(this.description, description);
    }

    public boolean isTypeChanged(ChannelType type) {
        return type != null && !Objects.equals(this.type, type);
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
