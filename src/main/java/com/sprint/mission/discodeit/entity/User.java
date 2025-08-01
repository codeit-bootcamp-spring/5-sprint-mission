package com.sprint.mission.discodeit.entity;

import java.util.Objects;
import java.util.UUID;

public class User extends BaseEntity {
    private String username;
    private String password;

    public User(String username, String password) {
        super(UUID.randomUUID(), System.currentTimeMillis(), System.currentTimeMillis());
        this.username = username;
        this.password = password;
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


    public void update(String username, String password) {
        if (username != null) {
            this.username = username;
        }
        if (password != null) {
            this.password = password;
        }
        // 업데이트 시각을 현재 시각으로 갱신합니다.
        this.updatedAt = System.currentTimeMillis();
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}
