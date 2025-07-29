package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Channel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    
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
        this.userIds = new HashSet<>();    // 멤버 Id를 저장할 HastSet 초기화
    }

    public UUID getId() { return id; }
    public long getCreatedAt() { return createdAt; }
    public long getUpdatedAt() { return updatedAt; }
    public String getName() { return channelName; }
    public Set<UUID> getUserIds() {
        return new  HashSet<>(userIds); // 채널 멤버 ID들의 불변 Set
    }

    public void updateName(String channelName) {
        this.channelName = channelName;
        this.updatedAt = System.currentTimeMillis();
    }

    public boolean join (UUID id) {
        this.updatedAt = System.currentTimeMillis();
        return this.userIds.add(id);
    }

    public boolean leave (UUID id) {
        this.updatedAt = System.currentTimeMillis();
        return this.userIds.remove(id);
    }

    public boolean isMember(UUID id) {
        this.updatedAt = System.currentTimeMillis();
        return this.userIds.contains(id);
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", channelName='" + channelName + '\'' +
                ", userCount=" + userIds.size() +
                '}';
    }
}
