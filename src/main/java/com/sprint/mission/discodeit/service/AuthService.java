package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.AuthLoginRequest;
import com.sprint.mission.discodeit.dto.response.AuthLoginResponse;

public interface AuthService {
        AuthLoginResponse login(AuthLoginRequest request);
}
