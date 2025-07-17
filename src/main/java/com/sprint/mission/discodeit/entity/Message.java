package com.sprint.mission.discodeit.entity;

import java.util.StringJoiner;
import java.util.UUID;

public class Message {
    private UUID id;
    private Long createdAt; // 생성시간
    private Long updatedAt; // 수정시간

    // 메세지 내용과 글쓴이 따로 추가
    private String content;
    private UUID authorId;

    public Message(String content) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = createdAt;
        this.content = content;
        this.authorId = UUID.randomUUID();
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

    public UUID getAuthorId() {
        return authorId;
    }

    public void update(UUID messageId, String content){
        this.content = content;
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Message.class.getSimpleName() + "[", "]")
                .add("id = " + id)
                .add("authorId = " + authorId)
                .add("content = '" + content + "'")
                .add("createdAt = " + createdAt)
                .add("updatedAt = " + updatedAt)
                .toString();
    }
}
