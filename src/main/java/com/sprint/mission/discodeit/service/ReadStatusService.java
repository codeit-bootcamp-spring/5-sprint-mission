// src/main/java/com/sprint/mission/discodeit/service/ReadStatusService.java

package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

  ReadStatusDto create(ReadStatusDto dto);

  ReadStatusDto update(UUID readStatusId, ReadStatusDto dto);

  List<ReadStatusDto> findAllByUserId(UUID userId);

  void deleteByChannelId(UUID channelId);

  ReadStatusDto findById(UUID id);
}