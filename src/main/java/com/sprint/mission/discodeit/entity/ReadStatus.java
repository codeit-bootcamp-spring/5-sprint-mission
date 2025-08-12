package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
public class ReadStatus extends BaseEntity{
    private UUID userId;
    private UUID channelId;
    private Instant lastReadAt;

    public ReadStatus(UUID userId, UUID channelId, Instant lastReadAt) {
        super();
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = lastReadAt;
    }

    public void update(Instant newReadAt) {
        if (newReadAt.isAfter(this.lastReadAt)) {
            this.lastReadAt = newReadAt;
            this.updatedAt = Instant.now();
        }
    }
}
