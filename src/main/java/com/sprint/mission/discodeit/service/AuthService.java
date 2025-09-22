package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.response.auth.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
