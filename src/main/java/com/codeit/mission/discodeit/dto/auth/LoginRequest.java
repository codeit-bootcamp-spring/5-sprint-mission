package com.codeit.mission.discodeit.dto.auth;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class LoginRequest {
    private final String username;
    private final String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
