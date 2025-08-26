package com.sprint.mission.discodeit.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@ToString
public class Channel implements Serializable {
    private static final long serialVersionUID = 1L;
    private UUID id;
    private final Instant createdAt;
    private Instant updatedAt;
    //
    private ChannelType type;
    private String name;
    private String description;

    private Instant lastMessageAt;
    private List<UUID> participantIds;

    public Channel(ChannelType type, String name, String description) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;

        //
        this.type = type;
        this.name = name;
        this.description = description;

        this.lastMessageAt = null;
        this.participantIds = new ArrayList<>();
    }

    public void update(String newName, String newDescription) {
        boolean anyValueUpdated = false;
        if (newName != null && !newName.equals(this.name)) {
            this.name = newName;
            anyValueUpdated = true;
        }
        if (newDescription != null && !newDescription.equals(this.description)) {
            this.description = newDescription;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }

    public void setLastMessageAt(Instant lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
        this.updatedAt = Instant.now();
    }

    public void addParticipant(UUID userId) {
        if (!participantIds.contains(userId)) {
            participantIds.add(userId);
        }
    }

    public void removeParticipant(UUID userId) {
        participantIds.remove(userId);
    }
}