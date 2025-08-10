package com.codeit.mission.discodeit.dto.readstatus;

import com.codeit.mission.discodeit.entity.ReadStatus;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public class ReadStatusResponse {

    private final UUID id;
    private final Instant createdAt;
    private final Instant updatedAt;

    private final Instant lastReadTime;
    private final UUID channelId;
    private final UUID userId;

    public ReadStatusResponse(ReadStatus readStatus) {
        this.id = readStatus.getId();
        this.createdAt = readStatus.getCreatedAt();
        this.updatedAt = readStatus.getUpdatedAt();
        this.lastReadTime = readStatus.getLastReadTime();
        this.channelId = readStatus.getChannelId();
        this.userId = readStatus.getUserId();
    }
}
