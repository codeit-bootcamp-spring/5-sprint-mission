package com.sprint.mission.discodeit.entity;

import java.util.List;
import java.util.UUID;

public class ChannelDTO {
    private UUID id;
    private User owner;
    private UUID ownerId;
    private String channelName;
    private boolean nsfw;

    public ChannelDTO(UUID id, String channelName, User owner, boolean nsfw) {
        this.id = id;
        this.owner = owner;
        this.ownerId = owner.getId();
        this.channelName = channelName;
        this.nsfw = nsfw;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public boolean isNsfw() {
        return nsfw;
    }

    public void setNsfw(boolean nsfw) {
        this.nsfw = nsfw;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ChannelDTO{");
        sb.append("id=").append(id);
        sb.append(", channelName='").append(channelName).append('\'');
        sb.append(", ownerId=").append(ownerId);
        sb.append(", owner=").append(owner);
        sb.append(", nsfw=").append(nsfw);
        sb.append('}');
        return sb.toString();
    }
}
