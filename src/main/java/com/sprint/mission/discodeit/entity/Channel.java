package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Channel {

    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;
    private String name;
    private String description;
    private int count;

    // 생성자, count는 채널 내의 유저 수로 처음 만들 때는 1이라 가정
    public Channel(String name, String description) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.count = 1;
        this.name = name;
        this.description = description;
    }

    // 반환 함수들
    public UUID getId() { return id; }
    public Long getCreatedAt() { return createdAt; }
    public Long getUpdatedAt() { return updatedAt; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getCount() { return count; }

    public void update(String name, String description, int count) {
        this.name = name;
        this.description = description;

        // 유저 수가 바뀌었을 때를 대비 업데이트 함수에 추가
        this.count = count;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", count=" + count +
                '}';
    }
}
