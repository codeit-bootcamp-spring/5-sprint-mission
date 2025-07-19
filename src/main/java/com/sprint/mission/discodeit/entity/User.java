package com.sprint.mission.discodeit.entity;

import java.util.Objects;
import java.util.UUID;

public class User {
    private final UUID id;
    private String username;
    private String password;
    private final Long createdAt;
    private Long updatedAt;

    public User(String username, String password) {
        long now = System.currentTimeMillis();
        this.id = UUID.randomUUID();
        this.username = username;
        this.password = password;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public void update(String newUsername, String newPassword) {
        this.username = newUsername;
        this.password = newPassword;
        this.updatedAt = System.currentTimeMillis();
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("id=").append(id);
        sb.append(", username='").append(username).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }
}
