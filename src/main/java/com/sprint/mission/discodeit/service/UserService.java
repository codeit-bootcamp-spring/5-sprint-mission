package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.neutral.UserCommand;
import com.sprint.mission.discodeit.dto.response.UserFindResponse;
import com.sprint.mission.discodeit.entity.User;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

public interface UserService {

  User create(@Valid UserCommand userCommand);

  UserFindResponse findById(UUID userId);

  List<UserFindResponse> findAll();

  User update(UUID userId, @Valid UserCommand userCommand);

  void delete(UUID userId);
}
