package com.sprint.mission.discodeit.service;

<<<<<<< HEAD
import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.response.UserResponseDto;

public interface AuthService {
    UserResponseDto login(LoginRequest loginRequest);
=======
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.LoginRequest;

public interface AuthService {

  UserDto login(LoginRequest loginRequest);
>>>>>>> 8a7ffb72 (feat: 스프린트 7 요구사항 구현)
}
