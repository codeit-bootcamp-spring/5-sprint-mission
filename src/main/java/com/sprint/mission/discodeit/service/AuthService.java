package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.AuthDto.Login;
import com.sprint.mission.discodeit.dto.UserDto;

public interface AuthService {

  UserDto.Detail login(Login login);
}
