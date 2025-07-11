package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User {

    private final UUID id;
    private final Long createdAt;
    private final List<UUID> channelIds =  new ArrayList<>(); // 채널에도 유저의 UUID를 모아놓은 것처럼 유저에도 참가한 채널의 UUID 담을 수 있는 리스트 선언
    private Long updatedAt;
    private String name;
    private int age;

    // 생성자
    public User(String name, int age) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.name = name;
        this.age = age;
    }

    // 반환 함수들
    public UUID getId() { return id; }
    public Long getCreatedAt() { return createdAt; }
    public List<UUID> getChannelIds() { return channelIds; }
    public Long getUpdatedAt() { return updatedAt; }
    public String getName() { return name; }
    public int getAge() { return age; }

    // 업데이트
    public void update(String name, int age) {
        this.name = name;
        this.age = age;
        this.updatedAt = System.currentTimeMillis();
    }

    // 채널 참가
    public boolean joinChannel(UUID channelId) {
        if (this.channelIds.contains(channelId)) {
            return false;
        }

        channelIds.add(channelId);
        this.updatedAt = System.currentTimeMillis(); // 채널 추가할 때도 시간 업데이트
        return true;
    }

    // 채널 퇴장
    public boolean leaveChannel(UUID channelId) {
        if (!this.channelIds.contains(channelId)) {
            return false;
        }

        channelIds.remove(channelId);
        this.updatedAt = System.currentTimeMillis();
        return true;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", channelIds=" + channelIds +
                '}';
    }
}
