package com.sprint.mission.discodeit.entity;

import java.util.StringJoiner;
import java.util.UUID;

public class User {
    private UUID id;
    private Long createdAt; // DB 타임스탬프
    private Long updatedAt; // 수정시간

    // 유저 이름 별도로 추가
    private String username;

    public User(String username) {
        this.id = UUID.randomUUID();
        this.username = username;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = createdAt;
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

    public String getUsername() {
        return username;
    }

    public void update(String username){
      this.username = username;
      this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", User.class.getSimpleName() + "[", "]")
                .add("id = " + id)
                .add("username = '" + username + "'")
                .add("createdAt = " + createdAt)
                .add("updatedAt = " + updatedAt)
                .toString();
    }
}
