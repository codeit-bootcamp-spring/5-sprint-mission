package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Long createdAt;
    private Long updatedAt;

    //내가 디스코드에서 필요한 필드를 추가적으로 설계하는 자리
    private String username;
    private String password;

    public User(String username, String password) {
        id = UUID.randomUUID();
        this.username = username;
        this.password = password;
        createdAt = System.currentTimeMillis();
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

    public String getPassword() {
        return password;
    }

    public void update(String username, String password){
    this.username = username;
    this.password = password;
    updatedAt = System.currentTimeMillis();

    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
