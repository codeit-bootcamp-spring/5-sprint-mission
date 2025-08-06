package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
public class Channel extends BaseEntity implements Serializable{
    private static final long serialVersionUID = 1L;

    private final ChannelType channelType;
    private final UUID ownerUserId;
    private String channelName;

    public Channel(
            String channelName, UUID ownerUserId, ChannelType channelType
    ) {
        super();
        this.channelName = channelName;
        this.ownerUserId = ownerUserId;
        this.channelType = channelType;
    }

    public void updateChannelName(String channelName) {
        if(!this.channelName.equals(channelName)){
            this.channelName = channelName;
            super.updateUpdatedAt();
        }

    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Channel{");
        sb.append("channelType=").append(channelType);
        sb.append(", ownerUserId=").append(ownerUserId);
        sb.append(", channelName='").append(channelName).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Channel channel = (Channel) o;
        return channelType == channel.channelType && Objects.equals(ownerUserId, channel.ownerUserId) && Objects.equals(channelName, channel.channelName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(channelType, ownerUserId, channelName);
    }
}
