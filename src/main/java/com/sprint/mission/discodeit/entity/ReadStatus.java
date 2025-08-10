package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus {
    // private final UUID id;
    // private final UUID userId;
    // private final UUID channelId;

    private Instant createdAt;
    private Instant updatedAt;
}
