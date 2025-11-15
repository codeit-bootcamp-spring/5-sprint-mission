package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;

import jakarta.validation.Valid;

public interface AuthService {

	UserDto login(@Valid LoginRequest loginRequest);
}
