package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class User {
    private final UUID id;
    private String nickName;
    private final long createdAt;
    private long updatedAt;


    public User(String nickName) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
        this.nickName = nickName;
    }

    public void updateNickName(String nickName){
        this.nickName = nickName;
        this.updatedAt = System.currentTimeMillis();
    }

    public UUID getId() {
        return id;
    }

    public String getNickName() {
        return nickName;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("id=").append(id);
        sb.append(", nickName='").append(nickName).append('\'');
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append('}');
        return sb.toString();
    }
}