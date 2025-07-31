package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable {
    private final UUID id;
    private final long createdAt;
    private final long updatedAt;
    private final String name;
    private final String email;
    private final String password;

    public User(UUID id, long createdAt, long updatedAt, String name, String email, String password) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public User(String name) {
        this(UUID.randomUUID(), System.currentTimeMillis(), System.currentTimeMillis(), name, "", "");
    }

    public User(String name, String email, String password) {
        this(UUID.randomUUID(), System.currentTimeMillis(), System.currentTimeMillis(), name, email, password);
    }

    // Getter
    public UUID getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public User withName(String name) {
        return new User(this.id, this.createdAt, System.currentTimeMillis(), name, this.email, this.password);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}

