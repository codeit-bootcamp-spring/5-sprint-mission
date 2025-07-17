package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.*;

public class Channel {
    private final UUID id;
    private UUID ownerId;

    private final Long createAt;
    private Long modifyAt;

    private final List<User> members;
    private String channelName;
    private boolean nsfw;

    public Channel(User user, String channelName, boolean nsfw) {
        this.id = UUID.randomUUID();
        Instant now = Instant.now();
        this.createAt = now.getEpochSecond();

        this.ownerId = user.getId();
        this.channelName = channelName;
        this.nsfw = nsfw;
        this.members = new ArrayList<>();
        members.add(user);
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

    public List<User> getMembers() { return members; }

    public String getChannelName() { return channelName; }

    public boolean isNsfw() { return nsfw; }

    public void update(Channel channelDTO) {
        this.ownerId = channelDTO.ownerId;
        this.channelName = channelDTO.channelName;
        this.nsfw = channelDTO.nsfw;
        this.members.clear();
        this.members.addAll(channelDTO.members);

        Instant now = Instant.now();
        modifyAt = now.getEpochSecond();
    }
}
