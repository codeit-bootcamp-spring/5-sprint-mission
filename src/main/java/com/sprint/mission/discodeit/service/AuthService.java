package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.response.user.LoginResponse;

public interface AuthService {
	LoginResponse login(LoginRequest request);
}
