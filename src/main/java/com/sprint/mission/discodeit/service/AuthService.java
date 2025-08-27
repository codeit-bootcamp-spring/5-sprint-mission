package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.UserLoginRequest;
import com.sprint.mission.discodeit.entity.User;
import jakarta.validation.Valid;

public interface AuthService {

  User login(@Valid UserLoginRequest userLoginRequest);
}
