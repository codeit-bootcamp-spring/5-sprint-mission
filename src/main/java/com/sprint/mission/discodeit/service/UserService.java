package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.neutral.UserCommand;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

public interface UserService {

  UserDto create(@Valid UserCommand userCommand);

  UserDto findById(UUID userId);

  List<UserDto> findAll();

  UserDto update(UUID userId, @Valid UserCommand userCommand);

  void delete(UUID userId);
}
