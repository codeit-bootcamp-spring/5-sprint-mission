package com.sprint.mission.discodeit.entity;

import java.util.Objects;
import java.util.UUID;

public class Message extends BaseEntity {
    private UUID channelId;
    private UUID authorId;
    private String content;

    public Message(UUID channelId, UUID authorId, String content) {
        this.channelId = channelId;
        this.authorId = authorId;
        this.content = content;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public UUID getAuthorId() {
        return authorId;
    }

    public String getContent() {
        return content;
    }

    public void editContent(String content) {
        this.content = content;

        setUpdatedAt(System.currentTimeMillis());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(channelId, message.channelId) && Objects.equals(authorId, message.authorId) && Objects.equals(content, message.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(channelId, authorId, content);
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
