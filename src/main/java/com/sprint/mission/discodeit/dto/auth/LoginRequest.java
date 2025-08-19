package com.sprint.mission.discodeit.dto.auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String userId;
    private String password;
}
