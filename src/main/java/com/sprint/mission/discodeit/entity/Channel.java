package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Channel {

    private UUID id; // 채널 고유 id
    private Long createdAt; // 생성 시간
    private Long updatedAt; // 수정 시간
    private String channelName; // 채널 이름

    public Channel(String channelName) {
        id = UUID.randomUUID();
        this.channelName = channelName;
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

    public String getChannelName() {
        return channelName;
    }

    public Channel update(String channelName) {
        this.channelName = channelName;
        this.updatedAt = System.currentTimeMillis();
        return this;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", channelName='" + channelName + '\'' +
                '}';
    }



}
