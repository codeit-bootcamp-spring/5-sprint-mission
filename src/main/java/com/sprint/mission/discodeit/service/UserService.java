package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserDto.Create;
import com.sprint.mission.discodeit.dto.UserDto.Update;
import java.util.List;
import java.util.UUID;

public interface UserService {

  UserDto.DetailResponse create(Create create);

  UserDto.DetailResponse update(Update update);

  UserDto.DetailResponse findById(UUID id);

  List<UserDto.DetailResponse> findAll();

  void delete(UUID id);

  void deleteAll();


}
