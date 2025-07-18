package com.sprint.mission.discodeit.entity;

import java.util.Objects;
import java.util.UUID;

public class User {

    private UUID id;
    private Long createdAt;
    private Long updatedAt;
    private String username;
    private String password;
    private String nickname;

    public User(String username, String password, String nickname) {
        id = UUID.randomUUID();
        this.username = username;
        this.password = password;
        this.nickname = nickname;
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

    public String getNickname() {
        return nickname;
    }

    public User update(String username, String password, String nickname) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.updatedAt = System.currentTimeMillis();
        return this;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(createdAt, user.createdAt) && Objects.equals(updatedAt, user.updatedAt) && Objects.equals(username, user.username) && Objects.equals(password, user.password) && Objects.equals(nickname, user.nickname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdAt, updatedAt, username, password, nickname);
    }
}
