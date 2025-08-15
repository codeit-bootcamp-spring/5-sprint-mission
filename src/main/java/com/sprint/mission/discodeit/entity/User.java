package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;
    //
    private UUID profileId;
    private String username;
    private String email;
    private String password;

    public User(String username, String email, String password, UUID profileId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();

        this.profileId = profileId;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public void update(String username, String email, String password, UUID profileId) {
        if (checkUpdated(username, email, password, profileId)) {
            this.updatedAt = Instant.now();
        }
    }

    private boolean checkUpdated(String username, String email, String password, UUID profileId) {
        boolean anyValueUpdated = false;

        if (!username.equals(this.username)) {
            this.username = username;
            anyValueUpdated = true;
        }
        if (!email.equals(this.email)) {
            this.email = email;
            anyValueUpdated = true;
        }
        if (!password.equals(this.password)) {
            this.password = password;
            anyValueUpdated = true;
        }
        if (profileId != null && !profileId.equals(this.profileId)) {
            this.profileId = profileId;
            anyValueUpdated = true;
        }

        return anyValueUpdated;
    }

}
