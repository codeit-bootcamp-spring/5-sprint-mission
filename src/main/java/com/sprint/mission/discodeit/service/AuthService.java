package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.response.LoginDto;

public interface AuthService {
    LoginDto login(String username, String password);
}
