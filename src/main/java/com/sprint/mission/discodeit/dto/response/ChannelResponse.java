package com.sprint.mission.discodeit.dto.response;

import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@ToString
@Getter
public class ChannelResponse {
    private final UUID id;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final String name;
    private final String description;
    private final String type;
    private final Instant latestMessageAt;
    private final List<UUID> participantIds;

    public ChannelResponse(UUID id, Instant createdAt, Instant updatedAt, String name, String description,
                           String type, Instant latestMessageAt, List<UUID> participantIds) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.name = name;
        this.description = description;
        this.type = type;
        this.latestMessageAt = latestMessageAt;
        this.participantIds = participantIds;
    }
}
