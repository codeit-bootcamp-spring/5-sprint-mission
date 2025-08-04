package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import java.util.UUID;

@Getter
public class User extends Base  {

    private String name;
    private final String email;
    private UUID profileId;
    @JsonIgnore private String password;

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public void updateName(String newName) {
        this.name = newName;
        updateTimestamp();
    }

    public void updateProfileId(UUID newProfileId) {
        this.profileId = newProfileId;
        updateTimestamp();
    }

    public void updatePassword(String password) {
        this.password = password;
        updateTimestamp();
    }

    @Override
    public String toString() {
        return String.format(
                "\n아이디: %-10s  이름: %-10s  이메일: %-10s",
                getId(), getName(), getEmail()
        );
    }
}
