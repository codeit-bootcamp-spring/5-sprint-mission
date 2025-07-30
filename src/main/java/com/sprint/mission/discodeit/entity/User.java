package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.UUID;

public class User {
    private final UUID id;
    private String username;
    private String password;
    private int age;
    private String email;

    private Long createdAt;
    private Long updatedAt;

    public User(String username, String password, int age, String email) {
        this.id = UUID.randomUUID();
        this.username = username;
        this.password = password;
        this.age = age;
        this.email = email;
        this.createdAt = Instant.now().getEpochSecond();
        this.updatedAt = createdAt;
    }

    public long update() {
        return this.updatedAt = Instant.now().getEpochSecond();
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAge() {
        return age;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", age=" + age +
                ", email='" + email + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}