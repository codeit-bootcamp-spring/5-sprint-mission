package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.auth.LoginDto;

public interface AuthService {

    LoginDto.response login(String email, String password);
}
