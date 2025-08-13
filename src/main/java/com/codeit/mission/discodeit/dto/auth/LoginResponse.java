package com.codeit.mission.discodeit.dto.auth;

import com.codeit.mission.discodeit.entity.User;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public class LoginResponse {

    private final UUID id;
    private final Instant createdAt;
    private final Instant updatedAt;

    private final String username;
    private final String email;

    public LoginResponse(User user) {
        this.id = user.getId();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
        this.username = user.getUsername();
        this.email = user.getEmail();
    }
}
