package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class User {
    private UUID id;
    private Long createdAt;
    private Long updatedAt;

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

    public Long getCreateAt() {
        return createdAt;
    }

    public Long getUpdateAt() {
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
