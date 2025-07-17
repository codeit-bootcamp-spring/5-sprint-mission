package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message {
    private UUID id;
    private Long createdAt;
    private Long updatedAt;

    // 소속된 채널과 글쓴이 표현
    private String channelName;
    private String nickName;
    private String message;

    public Message(String channelName,String nickName, String message) {
        this.id = UUID.randomUUID();
        this.channelName = channelName;
        this.nickName = nickName;
        this.message = message;
        this.createdAt = System.currentTimeMillis();
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

    public String getChannelName() {
        return channelName;
    }

    public String getNickName() {
        return nickName;
    }

    public String getMessage() {
        return message;
    }

    public void updateMessage(String message) {
        this.message = message;
        this.updatedAt = System.currentTimeMillis();
    }
    @Override
    public String toString() {
        String sb = "메세지{" + " 채널='" + channelName + '\'' +
                ", 전송 시간=" + createdAt +
                ", 닉네임='" + nickName + '\'' +
                ", 내용='" + message + '\'' +
                '}'+ '\n';
        return sb;
    }
}
