package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.user.LoginRequest;

public interface AuthService {
    UserDto login(LoginRequest loginRequest);
}
