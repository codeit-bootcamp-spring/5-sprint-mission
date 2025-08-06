package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus {

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;

    private UUID channelId;
    private UUID userId;

    public ReadStatus(User user, Channel channel) {
        this.id = UUID.randomUUID();
        this.userId = user.getId();
        this.channelId = channel.getId();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }
}
