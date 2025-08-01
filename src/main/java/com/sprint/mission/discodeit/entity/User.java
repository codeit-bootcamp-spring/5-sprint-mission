package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.UserDto;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class User extends BaseEntity {
    private String name;
    private String email;
    private String password;
    private UUID profileId;

    public User(String name, String email, String password, UUID profileId) {
        super();
        this.name = name;
        this.email = email;
        this.password = password;
        this.profileId = profileId;
    }

    public void update(String name, UUID profileId) {
        this.name = name;
        this.profileId = profileId;
    }


    public void update(String newName, String newEmail, String newPassword, UUID profileId) {
        boolean anyValueUpdated = false;

        if (newName != null && !newName.equals(this.name)) {
            this.name = newName;
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
        if(profileId != null && !profileId.equals(this.profileId)) {
            this.profileId = profileId;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            updateTimestamp();
        }
    }


    public static User of(UserDto.CreateRequest request, UUID profileId) {
        return new User(
            request.getName(),
            request.getEmail(),
            request.getPassword(),
            profileId
        );
    }

    @Override
    public String toString() {
        return "User{" +
            "name='" + name + '\'' +
            ", email='" + email + '\'' +
            ", password='" + password + '\'' +
            ", profileId=" + profileId +
            ", id=" + id +
            ", createdAt=" + createdAt +
            ", updatedAt=" + updatedAt +
            '}';
    }
}
