package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class Message implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;              //고유 아이디
    private String content;             //메시지 내용
    private UUID userId;                //작성자 아이디
    private UUID channelId;             //작성 채널 아이디
    private final Long createdAt;        //생성 시간
    private Long updatedAt;              //수정 시간

    public Message() {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
    }

    public Message(String content, UUID userId, UUID channelId) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.content = content;
        this.userId = userId;
        this.channelId = channelId;
    }

    public UUID getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "메시지 { " +
                "아이디 = " + id +
                ", 내용 = '" + content + '\'' +
                ", 사용자 ID = " + userId +
                ", 채널 ID = " + channelId +
                ", 생성 시간 = " + createdAt +
                ", 수정 시간 = " + updatedAt +
                " }";
    }
}
