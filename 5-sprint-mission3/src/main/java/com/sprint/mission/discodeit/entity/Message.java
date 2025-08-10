package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;

    private String content;

    private UUID channelId;
    private UUID authorId;
    private List<UUID> attachmentIds = new ArrayList<>();

    public Message(String content, UUID channelId, UUID authorId, List<UUID> attachmentIds) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();

        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;
        if (attachmentIds != null) {
            this.attachmentIds = attachmentIds;
        }
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

    public void addAttachment (UUID attachmentId) {
        if(!this.attachmentIds.contains(attachmentId)) {
            this.attachmentIds.add(attachmentId);
            this.updatedAt = Instant.now();
        }
    }

    public void removeAttachment (UUID attachmentId) {
        if(this.attachmentIds.contains(attachmentId)) {
            this.attachmentIds.remove(attachmentId);
            this.updatedAt = Instant.now();
        }
    }

    public void setAttachmentIds (List<UUID> attachmentIds) {
        if (!this.attachmentIds.equals(attachmentIds)) {
            this.attachmentIds.clear();
            this.attachmentIds.addAll(attachmentIds);
            this.updatedAt = Instant.now();
        }
    }
}
