package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.response.AuthResponse;
import com.sprint.mission.discodeit.entity.User;

public interface AuthService {

  AuthResponse login(LoginRequest loginRequest);
}
