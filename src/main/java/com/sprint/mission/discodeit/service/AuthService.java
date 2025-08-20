package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.response.UserResponseDto;

public interface AuthService {
    UserResponseDto login(LoginRequest loginRequest);
}
