package com.sprint.mission.discodeit.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Schema(name = "Channel")
public class Channel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "Channel ID", format = "uuid")
    private UUID id;
    @Schema(description = "생성 시각", format = "date-time")
    private Instant createdAt;
    @Schema(description = "수정 시각", format = "date-time")
    private Instant updatedAt;
    //
    @Schema(description = "채널 타입(PUBLIC/PRIVATE)", allowableValues = {"PUBLIC", "PRIVATE"})
    private ChannelType type;
    @Schema(description = "채널명")
    private String name;
    @Schema(description = "채널 설명")
    private String description;

    public Channel(ChannelType type, String name, String description) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        //
        this.type = type;
        this.name = name;
        this.description = description;
    }

    public void update(String newName, String newDescription) {
        boolean anyValueUpdated = false;
        if (newName != null && !newName.equals(this.name)) {
            this.name = newName;
            anyValueUpdated = true;
        }
        if (newDescription != null && !newDescription.equals(this.description)) {
            this.description = newDescription;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }
}
