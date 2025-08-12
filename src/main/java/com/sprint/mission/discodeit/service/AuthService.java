package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.auth.LoginResponseDto;

public interface AuthService {

    LoginResponseDto login(String email, String password);
}
