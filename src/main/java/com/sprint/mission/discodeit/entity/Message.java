package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    private String channelId;
    private String authorId;

    private UUID id;
    private Long createdAt;
    private Long updatedAt;

    //내가 디스코드에서 필요한 필드를 추가적으로 설계하는 자리
    private String content;


    public Message(String content, String channelId, String authorId) {
        id = UUID.randomUUID();
        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;
        createdAt = System.currentTimeMillis();
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

    public String getChannelId() {
        return channelId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void update(String content, String channelId, String authorId){
        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;
        updatedAt = System.currentTimeMillis();
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
