package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;

public class Channel implements Serializable {
    private final UUID id;
    private final Long createAt;
    private final ChannelType channelType;

    private UUID ownerId;
    private String channelName;
    private boolean nsfw;

    private Long modifyAt;

    public Channel(UUID ownerId, String channelName, ChannelType channelType, boolean nsfw) {
        this.id = UUID.randomUUID();
        Instant now = Instant.now();
        this.createAt = now.getEpochSecond();

        this.ownerId = ownerId;
        this.channelName = channelName;
        this.channelType = channelType;
        this.nsfw = nsfw;
    }

    public UUID getId() {
        return id;
    }

    public long getCreateAt() {
        return createAt;
    }

    public long getModifyAt() {
        return modifyAt;
    }

    public UUID getOwnerId() { return ownerId; }

//    public List<User> getMembers() { return members; }

    public ChannelType getChannelType() { return channelType; }

    public String getChannelName() { return channelName; }

    public boolean isNsfw() { return nsfw; }

    public void updateModifyAt() {
        Instant now = Instant.now();
        this.modifyAt = now.getEpochSecond();
    }


    public void update(UUID ownerId, String channelName, boolean nsfw) {
        int sameValueCount = 0;
        if(this.ownerId.equals(ownerId)) {
            System.out.println("[Alarm] : The original channel owner and the owner to be changed are the same.");
            sameValueCount++;
        }
        if(this.channelName.equals(channelName)) {
            System.out.println("[Alarm] : The original channel name and the channel name to be changed are the same.");
            sameValueCount++;
        }
        if(this.nsfw == nsfw) {
            System.out.println("[Alarm] : The original nsfw and the nsfw to be changed are the same.");
            sameValueCount++;
        }

        this.ownerId = ownerId;
        this.channelName = channelName;
        this.nsfw = nsfw;

        if (sameValueCount != 3) {
            updateModifyAt();
        }
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", channelName='" + channelName + '\'' +
                ", ownerId=" + ownerId +
                ", channelType=" + channelType +
                ", nsfw=" + nsfw +
                ", createAt=" + createAt +
                ", modifyAt=" + modifyAt +
                '}';
    }
}
