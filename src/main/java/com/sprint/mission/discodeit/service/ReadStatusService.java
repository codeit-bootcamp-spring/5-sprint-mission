package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.dto.ReadStatusDto.CreateCommand;
import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

  ReadStatusDto.Detail create(CreateCommand request);

  ReadStatusDto.Detail find(UUID id);

  ReadStatusDto.Detail update(UUID id);

  List<ReadStatusDto.Detail> findAllByUserId(UUID userId);

  void delete(UUID id);

  void deleteAll();
}
