package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    //
    private String username;
    private String email;
    private String password;
    private UUID profileId; // BinaryContent를 위존하기 위해 사용됨 (User, Message)

    public User (String username, String email, String password, UUID profileId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        //
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileId = profileId;
    }

    public void update(String newUsername, String newEmail, String newPassword, UUID profileId){
        boolean anyValueUpdated = false;

        if(newUsername != null && newUsername.equals(this.username)){
            this.username = newUsername;
            anyValueUpdated = true;
        }
        if(newEmail != null && newEmail.equals(this.email)){
            this.email = newEmail;
            anyValueUpdated = true;
        }
        if(newPassword != null && newPassword.equals(this.password)){
            this.password = newPassword;
            anyValueUpdated = true;
        }
        if (profileId != null && profileId.equals(this.profileId)){
            this.profileId = profileId;
            anyValueUpdated = true;
        }
        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }

    }
}
