package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class User extends BaseEntity {
    private String username;
    private String password;

    public User(String username, String password) {
        super(UUID.randomUUID(), Instant.now().getEpochSecond(), Instant.now().getEpochSecond());
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
        boolean anyValueUpdated = false;
        if (username != null && !username.equals(this.username)) {
            this.username = username;
            anyValueUpdated = true;
        }
        if (password != null && !password.equals(this.password)) {
            this.password = password;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now().getEpochSecond();
        }
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
