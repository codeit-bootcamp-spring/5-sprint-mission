package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.readstatus.ReadStatusUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatusDto create(ReadStatusCreateRequest request);
    ReadStatusDto find(UUID id);
    List<ReadStatusDto> findAllByUserId(UUID userId);
    ReadStatusDto update(UUID id, ReadStatusUpdateRequest request);
    void delete(UUID id);
}
