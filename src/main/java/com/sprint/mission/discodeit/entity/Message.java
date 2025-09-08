package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
public class Message extends BaseUpdatableEntity {
    private Channel channel;
    private User author;
    private String content;
    private List<BinaryContent> attachments;

    public Message(Channel channel, User author, String content, List<BinaryContent> attachments) {
        if (channel == null) {
            throw new IllegalArgumentException("Channel ID is required");
        }
        if (author == null) {
            throw new IllegalArgumentException("Author ID is required");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Content is required");
        }

        this.channel = channel;
        this.author = author;
        this.content = content;
        this.attachments = attachments;
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
