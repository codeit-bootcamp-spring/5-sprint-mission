package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.service.SoftDeletable;

import java.util.UUID;

public class Message implements SoftDeletable {

    // 소프트 삭제 여부 (true면 삭제된 상태)
    private boolean deleted = false;

    private final UUID id;              // 고유 아이디
    private final UUID userId;          // 작성한 유저 아이디
    private final UUID channelId;       // 작성된 채널 아이디
    private final Long createdAt;       // 생성일
    private Long updatedAt;             // 정보 업데이트일
    private String content;             // 내용

    // 생성자
    public Message(UUID userId, UUID channelId, String content) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.content = content;
        this.userId = userId;
        this.channelId = channelId;
    }

    // 반환 함수들
    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public UUID getChannelId() { return channelId; }
    public Long getCreatedAt() { return createdAt; }
    public Long getUpdatedAt() { return updatedAt; }
    public String getContent() { return content; }

    // 정보 업데이트
    public void update(String content) {
        this.content = content;
        this.updatedAt = System.currentTimeMillis();
    }

    // 소프트 삭제 여부 반환
    @Override
    public boolean isDeleted() {
        return this.deleted;
    }

    // 소프트 삭제 처리
    @Override
    public void delete() {
        deleted = true;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", userId=" + userId +
                ", channelId=" + channelId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", content='" + content + '\'' +
                '}';
    }
}
