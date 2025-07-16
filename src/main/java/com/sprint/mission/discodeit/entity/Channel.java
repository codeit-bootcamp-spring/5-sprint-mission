package com.sprint.mission.discodeit.entity;

import java.util.Date;
import java.util.UUID;

public class Channel {
    private final UUID id;
    private final Long createdAt;
    private final UUID creatorUserId;  // 생성자 userId 추가
    private Long updatedAt;
    private String name;

    public Channel(String name, UUID creatorUserId) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.name = name;
        this.creatorUserId = creatorUserId;
    }

    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getCreatorUserId() {
        return creatorUserId;
    }

    public void update(String name) {
        this.name = name;
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return String.format(
                "[UUID: %s]\n[채널명: %s]\n[생성일: %s]\n[수정일: %s]\n[생성자 UUID: %s]",
                id,
                name,
                new Date(createdAt),
                updatedAt == null ? "없음" : new Date(updatedAt),
                creatorUserId
        );
    }
}
