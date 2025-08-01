package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class Channel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String channelName;
    private final String creatorUser;
    private final long createdAt;
    private long updatedAt;

    public Channel(String channelName, String creatorUser) {
        this.id = UUID.randomUUID().toString();
        this.channelName = channelName;
        this.creatorUser = creatorUser;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
    }

    public void update() {
        this.updatedAt = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
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
