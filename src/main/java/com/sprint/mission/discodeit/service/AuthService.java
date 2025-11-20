package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import jakarta.validation.Valid;

public interface AuthService {

  UserDto updateRole(@Valid UserRoleUpdateRequest request);
}
