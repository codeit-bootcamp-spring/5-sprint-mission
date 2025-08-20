package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserDto.CreateCommand;
import com.sprint.mission.discodeit.dto.UserDto.UpdateCommand;
import java.util.List;
import java.util.UUID;

public interface UserService {

  UserDto.Detail create(CreateCommand create);

  UserDto.Detail update(UpdateCommand update);

  UserDto.Detail findById(UUID id);

  List<UserDto.Detail> findAll();

  void delete(UUID id);

  void deleteAll();


}
