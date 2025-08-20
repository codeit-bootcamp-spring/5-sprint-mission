package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.dto.UserStatusDto.Create;
import java.util.List;
import java.util.UUID;

public interface UserStatusService {


  UserStatusDto.Detail create(Create request);

  UserStatusDto.Detail find(UUID id);

  List<UserStatusDto.Detail> findAll();

  UserStatusDto.Detail update(UUID id);

  UserStatusDto.Detail updateByUserId(UUID userId);

  void delete(UUID id);

  void deleteAll();
}
