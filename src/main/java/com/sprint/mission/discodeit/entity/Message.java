package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
public class Message extends BaseUpdatableEntity {
    private UUID channelId;
    private UUID authorId;
    private String content;
    private List<UUID> attachmentIds; // BinaryFile이 messageId를 갖고 있는게 낫지 않나

    public Message(UUID channelId, UUID authorId, String content, List<UUID> attachmentIds) {
        if (channelId == null) {
            throw new IllegalArgumentException("Channel ID is required");
        }
        if (authorId == null) {
            throw new IllegalArgumentException("Author ID is required");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Content is required");
        }

        this.channelId = channelId;
        this.authorId = authorId;
        this.content = content;
        this.attachmentIds = attachmentIds;
    }

    public void editContent(String content) {
        boolean anyValueUpdated = false;
        if (content != null && !content.equals(this.content)) {
            this.content = content;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.setUpdatedAt(Instant.now());
        }
    }
}
