package com.sprint.mission.discodeit.entity.main;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import static java.time.Instant.*;

@Getter
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private UUID profileId;

    private String username;
    private String email;
    private String password;

    private final Instant createdAt;
    private Instant updatedAt;

    public User(String username, String email, String password) {
        this.id = UUID.randomUUID();
        this.createdAt = now();

        this.username = username;
        this.email = email;
        this.password = password;
    }

    public void update(String newUsername, String newEmail, String newPassword) {
        boolean anyValueUpdated = false;

        if(isNameChanged(newUsername)) {
            this.username = newUsername;
            anyValueUpdated = true;
        }

        if(isEmailChanged(newEmail)) {
            this.email = newEmail;
            anyValueUpdated = true;
        }

        if(isPasswordChanged(newPassword)) {
            this.password = newPassword;
            anyValueUpdated = true;
        }

        if(anyValueUpdated) {
            this.updatedAt = now();
        }
    }

    public boolean isNameChanged(String name) {
        return name != null && !Objects.equals(this.username, name);
    }

    public boolean isEmailChanged(String email) {
        return email != null && !Objects.equals(this.email, email);
    }

    public boolean isPasswordChanged(String password) {
        return password != null && !Objects.equals(this.password, password);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", name='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
