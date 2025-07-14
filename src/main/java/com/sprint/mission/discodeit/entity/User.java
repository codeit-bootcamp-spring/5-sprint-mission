package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class User {
    private final UUID id;  // 객체의 고유 식별자
    private final long createdAt;  // 객체 생성 시간 -> 유닉스 타임스탬프로 나타내기 위한 필드
    private long updatedAt;// 객체 수정 시간 -> 유닉스 타임스탬프로 나타내기 위한 필드

    private String nickName; // 닉네임

    public User(String nickName) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis(); // 유닉스 타임스탬프
        this.updatedAt = this.createdAt; // createdAt -> updatedAt?
        this.nickName = nickName;
    }

    public void updateNickName(String nickName){
        this.nickName = nickName;
        this.updatedAt = System.currentTimeMillis();
    }

    public UUID getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public String getNickName() {
        return nickName;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("id=").append(id);
        sb.append(", nickName='").append(nickName);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt).append('\'');;
        sb.append('}');
        return sb.toString();
    }
}