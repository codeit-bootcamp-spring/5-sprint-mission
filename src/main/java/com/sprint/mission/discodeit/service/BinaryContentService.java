package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.BinaryContentDto.Create;
import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

  BinaryContentDto.DetailResponse create(Create request);

  BinaryContentDto.DetailResponse find(UUID id);

  List<BinaryContentDto.DetailResponse> findAllByIdIn(List<UUID> ids);

  void delete(UUID id);

  void deleteAll();
}
