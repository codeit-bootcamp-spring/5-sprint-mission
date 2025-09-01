package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

  ReadStatusDto create(@Valid ReadStatusCreateRequest readStatusCreateRequest);

  ReadStatusDto findById(UUID id);

  List<ReadStatusDto> findAllByUserId(UUID userId);

  ReadStatusDto update(UUID readStatusId, @Valid ReadStatusUpdateRequest readStatusUpdateRequest);

  void delete(UUID id);
}
