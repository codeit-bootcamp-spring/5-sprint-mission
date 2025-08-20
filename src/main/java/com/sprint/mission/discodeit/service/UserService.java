package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserDto.Create;
import com.sprint.mission.discodeit.dto.UserDto.Update;
import java.util.List;
import java.util.UUID;

public interface UserService {

  UserDto.Detail create(Create create);

  UserDto.Detail update(Update update);

  UserDto.Detail findById(UUID id);

  List<UserDto.Detail> findAll();

  void delete(UUID id);

  void deleteAll();


}
