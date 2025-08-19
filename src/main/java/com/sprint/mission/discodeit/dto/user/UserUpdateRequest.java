package com.sprint.mission.discodeit.dto.user;

import lombok.Data;
import lombok.ToString;

import java.util.UUID;

@Data
@ToString
public class UserUpdateRequest {
    private UUID id;
    private String userId;
    private String email;
    private String password;

    public UserUpdateRequest(UUID id, String userId, String email, String password) {
        this.id = id;
        this.userId = userId;
        this.email = email;
        this.password = password;
    }

    public UserUpdateRequest() {
    }
    
}
