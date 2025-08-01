package com.sprint.mission.discodeit.entity;

import java.util.Objects;
import java.util.UUID;

import static java.time.Instant.*;

public class User {

    private final UUID id;
    private final Long createdAt;

    private Long updatedAt;
    private String name;
    private String email;
    private String password;

    public User(String name, String email, String password) {
        this.id = UUID.randomUUID();
        this.createdAt = now().getEpochSecond();

        this.name = name;
        this.email = email;
        this.password = password;
        this.updatedAt = now().getEpochSecond();
    }

    public UUID getId() { return id; }
    public Long getCreatedAt() { return createdAt; }
    public Long getUpdatedAt() { return updatedAt; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    public void update(String name, String email, String password) {
        boolean anyValueUpdated = false;

        if(isNameChanged(name)) {
            this.name = name;
            anyValueUpdated = true;
        }

        if(isEmailChanged(email)) {
            this.email = email;
            anyValueUpdated = true;
        }

        if(isPasswordChanged(password)) {
            this.password = password;
            anyValueUpdated = true;
        }

        if(anyValueUpdated) {
            this.updatedAt = now().getEpochSecond();
        }
    }

    public boolean isNameChanged(String name) {
        return name != null && Objects.equals(this.name, name);
    }

    public boolean isEmailChanged(String email) {
        return email != null && Objects.equals(this.email, email);
    }

    public boolean isPasswordChanged(String password) {
        return password != null && Objects.equals(this.password, password);
    }
}
