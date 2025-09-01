package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.neutral.NewBinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContent;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

  BinaryContent create(@Valid NewBinaryContent newBinaryContent);

  BinaryContentDto findById(UUID id);

  List<BinaryContent> findAllByIdIn(List<UUID> ids);

  void delete(UUID id);
}
