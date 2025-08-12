package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Channel extends BaseEntity implements Serializable{
    private static final long serialVersionUID = 1L;

    private final ChannelType channelType;
    private final UUID ownerUserId;
    private String name;
    private String description;

    public Channel(
            String name, UUID ownerUserId, ChannelType channelType, String description
    ) {
        super();
        this.name = name;
        this.ownerUserId = ownerUserId;
        this.channelType = channelType;
        this.description = description;
    }

    public void updateChannelName(String channelName) {
        if(!this.name.equals(channelName)){
            this.name = channelName;
            super.updateUpdatedAt();
        }

    }

    public void updateDescription(String description) {
        if(!this.description.equals(description)){
            this.description = description;
            super.updateUpdatedAt();
        }
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Channel{");
        sb.append("channelType=").append(channelType);
        sb.append(", ownerUserId=").append(ownerUserId);
        sb.append(", name='").append(name).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Channel channel = (Channel) o;
        return channelType == channel.channelType && Objects.equals(ownerUserId, channel.ownerUserId) && Objects.equals(name, channel.name) && Objects.equals(description, channel.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(channelType, ownerUserId, name, description);
    }
}
