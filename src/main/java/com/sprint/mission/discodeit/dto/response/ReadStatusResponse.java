package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.ReadStatus;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@ToString
@Getter
public class ReadStatusResponse {
    private final UUID id;
    private final UUID userId;
    private final UUID channelId;
    private final Instant lastReadAt;
    private final Instant createdAt;
    private final Instant updatedAt;

    public ReadStatusResponse(ReadStatus readStatus) {
        this.id = readStatus.getId();
        this.userId = readStatus.getUserId();
        this.channelId = readStatus.getChannelId();
        this.lastReadAt = readStatus.getLastReadAt();
        this.createdAt = readStatus.getCreatedAt();
        this.updatedAt = readStatus.getUpdatedAt();
    }
}
