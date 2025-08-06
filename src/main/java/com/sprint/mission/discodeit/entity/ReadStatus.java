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
}
