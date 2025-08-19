package com.sprint.mission.discodeit.dto.user;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class UserCreateRequest {

    private String userId;
    private String email;
    private String password;

    public UserCreateRequest() {
    }


    public UserCreateRequest(String userId, String email, String password) {
        this.userId = userId;
        this.email = email;
        this.password = password;
    }
    
}
