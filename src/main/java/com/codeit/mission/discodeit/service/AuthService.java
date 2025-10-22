package com.codeit.mission.discodeit.service;

import com.codeit.mission.discodeit.dto.data.UserDto;
import com.codeit.mission.discodeit.dto.request.LoginRequest;

public interface AuthService {

    UserDto login(LoginRequest loginRequest);
}
