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
