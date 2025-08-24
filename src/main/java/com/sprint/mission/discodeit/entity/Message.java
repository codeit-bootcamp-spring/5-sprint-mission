package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Message extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private UUID channelId;
    private UUID authorId;
    private String content;

    public Message(UUID channelId, UUID authorId, String content) {
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
    }

    public void editContent(String content) {
        if (content == null || content.equals(this.content)) {
            return;
        }

        this.content = content;
        this.setUpdatedAt(System.currentTimeMillis());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Message message = (Message) o;
        return Objects.equals(getId(), message.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return super.toString() + "Message{" +
                "channelId=" + channelId +
                ", authorId=" + authorId +
                ", content='" + content + '\'' +
                '}';
    }
}
