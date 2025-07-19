package com.sprint.mission.discodeit.entity;

import java.util.Objects;

public class Channel {
    private final String channelName;
    private final String creatorUser;
    private final Long createdAt;
    private Long updatedAt;

    public Channel(String name, String creatorUser) {
        long now = System.currentTimeMillis();
        this.channelName = name;
        this.creatorUser = creatorUser;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getCreatorUser() {
        return creatorUser;
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Channel{");
        sb.append("name='").append(channelName).append('\'');
        sb.append(", ownerUserId='").append(creatorUser).append('\'');
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Channel channel = (Channel) o;
        return Objects.equals(channelName, channel.channelName) && Objects.equals(creatorUser, channel.creatorUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(channelName, creatorUser);
    }
}
