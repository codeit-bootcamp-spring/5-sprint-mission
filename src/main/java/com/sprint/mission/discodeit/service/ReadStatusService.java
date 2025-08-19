package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatus create(@Valid ReadStatusCreateRequest readStatusCreateRequest);

    ReadStatus findById(UUID id);

    List<ReadStatus> findAllByUserId(UUID userId);

    ReadStatus update(@Valid ReadStatusUpdateRequest readStatusUpdateRequest);

    void delete(UUID id);
}
