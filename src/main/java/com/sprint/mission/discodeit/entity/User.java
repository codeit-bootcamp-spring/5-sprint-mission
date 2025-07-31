package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class User {
    //final 쓰는 이유: 변경x객체여서
    private final UUID id;
    private final long createdAt;
    private long updatedAt;
    private String nickname;

    //생성자. 객체 생성시 id, createdAt, updatedAt, nickname 초기화
    public User(String nickname) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
        this.nickname = nickname;
    }

    public UUID getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdateAt() {
        return updatedAt;
    }

    public String getNickname() {
        return nickname;
    }

    //void 사용 이유: 행동만 수행하고 결과값 반환하지 않기 때문에..
    public void updateNickname(String nickname) {
        this.nickname = nickname;
        this.updatedAt = System.currentTimeMillis();
    }
}