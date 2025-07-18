package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Channel {
    private UUID id;
    private Long createdAt;
    private Long updatedAt;

    // 내가 디스코드에서 필요한 필드를 추가적으로 설계하는 자리
    private String channelName; // 해당 채널(서버)의 이름
    private int channelOrder; //

    public Channel(String channelName, int channelOrder) {
        id = UUID.randomUUID();
        this.channelName = channelName;
        this.channelOrder = channelOrder;
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

    public int getChannelOrder() {
        return channelOrder;
    }

    public void updateId(UUID id) {
        this.id = id;
    }

    public void updateUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void updateChannelName(String channelName) {
        this.channelName = channelName;
    }

    public void updateChannelOrder(int channelOrder) {
        this.channelOrder = channelOrder;
    }
}
