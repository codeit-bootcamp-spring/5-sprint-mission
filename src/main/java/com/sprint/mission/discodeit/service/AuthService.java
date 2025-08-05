package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.AuthDto;

public interface AuthService {

    AuthDto.View login(String email, String password);
}
