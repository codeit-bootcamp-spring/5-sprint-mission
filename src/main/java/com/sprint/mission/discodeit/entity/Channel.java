package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Channel {
    private final UUID id;
    private String channelName;
    private String channelDescription;
    private final Long createAt;
    private Long updateAt;

    public Channel(UUID id, String channelName, String channelDescription, Long createAt) {
        this.id = id;
        this.channelName = channelName;
        this.channelDescription = channelDescription;
        this.createAt = createAt;
    }

    public UUID getId() {
        return id;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getChannelDescription() {
        return channelDescription;
    }

    public Long getCreateAt() {
        return createAt;
    }

    public Long getUpdateAt() {
        return updateAt;
    }

    public void updateChannel(String channelName, String channelDescription, Long updateAt) {
        this.channelName = channelName;
        this.channelDescription = channelDescription;
        this.updateAt = updateAt;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("채널 아이디: ").append(id).append("\n")
                .append("채널 이름: ").append(this.channelName).append("\n")
                .append("채널 설명: ").append(this.channelDescription).append("\n")
                .append("채널 생성일: ").append(this.createAt).append("\n")
                .append("채널 변경일: ").append(this.updateAt).append("\n");

        return sb.toString();
    }
}
