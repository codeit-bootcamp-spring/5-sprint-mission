package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Channel {

    private final UUID id;
    private final Long createdAt;
    private final List<UUID> userIds;       // 채널 참가한 유저의 UUID 담을 수 있는 리스트 선언
    private Long updatedAt;
    private String name;
    private String description;

    // 생성자, count는 채널 내의 유저 수로 처음 만들 때는 1이라 가정
    public Channel(List<UUID> userIds, String name, String description) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.userIds = userIds;
        this.updatedAt = System.currentTimeMillis();
        this.name = name;
        this.description = description;
    }

    // 반환 함수들
    public UUID getId() { return id; }
    public Long getCreatedAt() { return createdAt; }
    public List<UUID> getUserIds() { return userIds; }
    public Long getUpdatedAt() { return updatedAt; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getCount() { return userIds.size(); }        // 채널의 유저수는 userIds.size()로 가능하니 기존 count필드는 삭제

    public void update(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // 유저 추가
    public boolean addUser(UUID userId) {
        if (this.userIds.contains(userId)) {
            return false;
        }

        userIds.add(userId);
        this.updatedAt = System.currentTimeMillis(); // 유저 추가할 때도 시간 업데이트
        return true;
    }

    // 유저 제거
    public boolean removeUser(UUID userId) {
        if (!this.userIds.contains(userId)) {
            return false;
        }

        userIds.remove(userId);
        this.updatedAt = System.currentTimeMillis();
        return true;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", count =" + userIds.size() +
                ", userIds=" + userIds +
                '}';
    }
}
