package com.sprint.mission.discodeit.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
@Schema(name = "UserStatus")
public class UserStatus implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "UserStatus ID", format = "uuid")
    private UUID id;
    @Schema(description = "생성 시각", format = "date-time")
    private Instant createdAt;
    @Schema(description = "수정 시각", format = "date-time")
    private Instant updatedAt;
    //
    @Schema(description = "User ID", format = "uuid")
    private UUID userId;
    @Schema(description = "마지막 활동 시각", format = "date-time")
    private Instant lastActiveAt;
    @Schema(description = "온라인 여부")
    private Boolean online;

    public UserStatus(UUID userId, Instant lastActiveAt) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        //
        this.userId = userId;
        this.lastActiveAt = lastActiveAt;
    }

    public void update(Instant lastActiveAt) {
        boolean anyValueUpdated = false;
        if (lastActiveAt != null && !lastActiveAt.equals(this.lastActiveAt)) {
            this.lastActiveAt = lastActiveAt;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }

    public Boolean isOnline() {
        Instant instantFiveMinutesAgo = Instant.now().minus(Duration.ofMinutes(5));

        return lastActiveAt.isAfter(instantFiveMinutesAgo);
    }
}
