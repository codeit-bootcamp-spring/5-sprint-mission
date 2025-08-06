package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.StringJoiner;
import java.util.UUID;

public class Message implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Long createdAt; // 생성시간
    private Long updatedAt; // 수정시간

    private String content;
    private UUID channelId;
    private UUID authorId;

    public Message(String content, UUID channelId, UUID authorId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now().getEpochSecond();
        this.updatedAt = createdAt;
        this.channelId = channelId;
        this.authorId = authorId;
        this.content = content;
    }


    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public String getContent() {
        return content;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public UUID getAuthorId() {
        return authorId;
    }

    public void update(String content){
        boolean flag = false;
        if(content != null && !content.equals(this.content)){
            this.content = content;
            flag = true;
        }
        if(flag){
            this.updatedAt = Instant.now().getEpochSecond();
        }
    }


    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", content='" + content + '\'' +
                ", channelId=" + channelId +
                ", authorId=" + authorId +
                '}';
    }
}
