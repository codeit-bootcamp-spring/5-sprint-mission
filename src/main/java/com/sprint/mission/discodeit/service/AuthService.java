package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.user.UserLoginRequest;
import com.sprint.mission.discodeit.entity.User;

public interface AuthService {
    User login(UserLoginRequest userLoginRequest);
}
