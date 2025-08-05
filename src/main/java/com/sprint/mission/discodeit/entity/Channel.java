package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Channel extends BaseEntity implements Serializable{
    private static final long serialVersionUID = 1L;

    private final UUID ownerUserId;
    private String channelName;
    private final Set<UUID> usersId = new HashSet<>();
    private final Set<UUID> messagesId = new HashSet<>();

    public Channel(
            String channelName, UUID ownerUserId
    ) {
        super();
        this.channelName = channelName;
        this.ownerUserId = ownerUserId;
    }

    public UUID getOwnerUserId() {
        return ownerUserId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void updateChannelName(String channelName) {
        if(!this.channelName.equals(channelName)){
            this.channelName = channelName;
            super.updateUpdatedAt();
        }

    }

    public void addUserId(UUID userId) {
        usersId.add(userId);
    }

    public void addMessageId(UUID messageId) {
        messagesId.add(messageId);
    }
    public void removeUserId(UUID userId) {
        usersId.remove(userId);
    }
    public void removeMessageId(UUID messageId) {
        messagesId.remove(messageId);
    }

    public Set<UUID> getUsersId() {
        return usersId;
    }

    public Set<UUID> getMessagesId() {
        return messagesId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Channel channel = (Channel) o;
        return Objects.equals(ownerUserId, channel.ownerUserId) && Objects.equals(channelName, channel.channelName) && Objects.equals(usersId, channel.usersId) && Objects.equals(messagesId, channel.messagesId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ownerUserId, channelName, usersId, messagesId);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Channel{");
        sb.append("ownerUserId=").append(ownerUserId);
        sb.append(", channelName='").append(channelName).append('\'');
        sb.append(", usersId=").append(usersId);
        sb.append(", messagesId=").append(messagesId);
        sb.append('}');
        return sb.toString();
    }
}
