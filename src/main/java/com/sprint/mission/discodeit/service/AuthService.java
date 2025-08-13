package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.LoginRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;

public interface AuthService {

    /**
     *
     * @return 일치하는 유저가 있는 경우 {@link UserResponse} 반환
     */
    UserResponse login(LoginRequest loginRequest);
}
