package com.sprint.mission.discodeit.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class User extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID profileId;
    private String username;
    private String password;
    private String email;

    public User(String username, String password, String email,UUID profileId) {
        super();
        this.username=username;
        this.password=password;
        this.email=email;
        this.profileId=profileId;
    }

    public void update(String newUsername,String newPassword,String newEmail, UUID newProfileId){
        boolean anyValueUpdated = false;
        if (newUsername != null && !newUsername.equals(this.username)) {
            this.username = newUsername;
            anyValueUpdated = true;
        }
        if (newEmail != null && !newEmail.equals(this.email)) {
            this.email = newEmail;
            anyValueUpdated = true;
        }
        if (newPassword != null && !newPassword.equals(this.password)) {
            this.password = newPassword;
            anyValueUpdated = true;
        }
        if (newProfileId != null && !newProfileId.equals(this.profileId)) {
            this.profileId = newProfileId;
            anyValueUpdated = true;
        }
        if (anyValueUpdated) {
            super.updateTimestamp();
        }
    }
}
