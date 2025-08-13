package com.codeit.mission.discodeit.service;

import com.codeit.mission.discodeit.dto.auth.LoginRequest;
import com.codeit.mission.discodeit.dto.auth.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}