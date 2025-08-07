package com.sprint.mission.discodeit.dto.response;

import lombok.Getter;

import java.util.UUID;

@Getter
public class UserResponse {
    private UUID id;
    private String username;
    private String email;
    private boolean online;

    public UserResponse(UUID id, String username, String email, boolean online) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.online = online;
    }
}
