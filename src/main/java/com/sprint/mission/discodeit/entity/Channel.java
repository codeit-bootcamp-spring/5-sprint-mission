package com.sprint.mission.discodeit.entity;

import java.util.*;

public class Channel {
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;

    private String channelName;
    private final Set<UUID> userIds;

    public Channel(String channelName) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;   // channel이름이 수정되면 시간을 타임스탬프로 나타내자
        this.channelName = channelName;
        this.userIds = new HashSet<>();
    }

    public UUID getId() { return id; }
    public long getCreatedAt() { return createdAt; }
    public long getUpdatedAt() { return updatedAt; }
    public String getName() { return channelName; }
    public Set<UUID> getUserIds() { return userIds; }

    public void updateName(String channelName) {
        this.channelName = channelName;
        this.updatedAt = System.currentTimeMillis();
    }

    public void join (UUID id) {
        userIds.add(id);
        this.updatedAt = System.currentTimeMillis();
    }

    public void leave (UUID id) {
        userIds.remove(id);
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Channel{");
        sb.append("id=").append(id);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", channelName='").append(channelName).append('\'');
        sb.append(", userIds=").append(userIds);
        sb.append('}');
        return sb.toString();
    }
}
