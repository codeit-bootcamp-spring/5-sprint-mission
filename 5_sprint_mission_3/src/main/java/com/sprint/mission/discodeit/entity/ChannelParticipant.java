package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public class ChannelParticipant implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private UUID channelId;
    private UUID userId;
    private Instant joinAt;

    public ChannelParticipant(UUID channelId, UUID userId) {
        this.id = UUID.randomUUID();
        this.channelId = channelId;
        this.userId = userId;
        this.joinAt = Instant.now();
    }
}
