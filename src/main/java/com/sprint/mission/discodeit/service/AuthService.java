package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.auth.request.LoginRequest;
import com.sprint.mission.discodeit.entity.main.User;

public interface AuthService {
    User login(LoginRequest loginRequest);
}
