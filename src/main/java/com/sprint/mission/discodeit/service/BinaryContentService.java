package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

  BinaryContentResponse create(BinaryContentCreateRequest request);

  BinaryContent find(UUID id);

  BinaryContent findById(UUID id);

  List<BinaryContent> findAllByIdIn(List<UUID> ids);

  void delete(UUID id);
}
