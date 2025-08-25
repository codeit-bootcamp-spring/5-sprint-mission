package com.sprint.mission.discodeit.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Schema(name = "Message")
public class Message implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "Message ID", format = "uuid")
    private UUID id;
    @Schema(description = "생성 시각", format = "date-time")
    private Instant createdAt;
    @Schema(description = "수정 시각", format = "date-time")
    private Instant updatedAt;
    //
    @Schema(description = "메시지 내용")
    private String content;
    //
    @Schema(description = "Channel ID", format = "uuid")
    private UUID channelId;
    @Schema(description = "작성자 User ID", format = "uuid")
    private UUID authorId;
    @Schema(description = "첨부 파일 ID 목록", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<UUID> attachmentIds;

    public Message(String content, UUID channelId, UUID authorId, List<UUID> attachmentIds) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        //
        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;
        this.attachmentIds = attachmentIds;
    }

    public void update(String newContent) {
        boolean anyValueUpdated = false;
        if (newContent != null && !newContent.equals(this.content)) {
            this.content = newContent;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }
}
