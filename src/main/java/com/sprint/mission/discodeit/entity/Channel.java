package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Channel {
    private UUID id;
    private Long createdAt;
    private Long updatedAt;

    // 내가 디스 코드에서 필요한 필드를 추가적으로 설계하는 자리
    private String channelName;
    private String description;

    public Channel(String channelName, String description) {
        this.channelName = channelName;
        this.description = description;
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
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

    public String getDescription() {
        return description;
    }
    public void updateChannel(String channelName, String description) {
        this.channelName = channelName;
        this.description = description;
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        String sb = "Channel{" + "채널이름='" + channelName + '\'' +
                ", 채널소개='" + description + '\'' +
                '}'+'\n';
        return sb;
    }

}
