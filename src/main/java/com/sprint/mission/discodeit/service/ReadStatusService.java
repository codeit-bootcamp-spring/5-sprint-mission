package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.status.read.CreateReadStatusRequest;
import com.sprint.mission.discodeit.dto.status.read.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.status.read.UpdateReadStatusRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatusResponse create(CreateReadStatusRequest request);
    Optional<ReadStatusResponse> getById(UUID id);
    List<ReadStatusResponse> getAllByUserId(UUID userId);
    ReadStatusResponse update(UpdateReadStatusRequest request);
    boolean delete(UUID id);
}
