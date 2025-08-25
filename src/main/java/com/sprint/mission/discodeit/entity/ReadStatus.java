package com.sprint.mission.discodeit.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Schema(name = "ReadStatus")
public class ReadStatus implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "ReadStatus ID", format = "uuid")
    private UUID id;
    @Schema(description = "생성 시각", format = "date-time")
    private Instant createdAt;
    @Schema(description = "수정 시각", format = "date-time")
    private Instant updatedAt;
    //
    @Schema(description = "User ID", format = "uuid")
    private UUID userId;
    @Schema(description = "Channel ID", format = "uuid")
    private UUID channelId;
    @Schema(description = "마지막 읽은 시각", format = "date-time")
    private Instant lastReadAt;

    public ReadStatus(UUID userId, UUID channelId, Instant lastReadAt) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        //
        this.userId = userId;
        this.channelId = channelId;
        this.lastReadAt = lastReadAt;
    }

    public void update(Instant newLastReadAt) {
        boolean anyValueUpdated = false;
        if (newLastReadAt != null && !newLastReadAt.equals(this.lastReadAt)) {
            this.lastReadAt = newLastReadAt;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }
}
