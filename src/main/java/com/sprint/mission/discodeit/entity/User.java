package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import static java.time.Instant.*;

@Getter
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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
        return name != null && !Objects.equals(this.name, name);
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
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
