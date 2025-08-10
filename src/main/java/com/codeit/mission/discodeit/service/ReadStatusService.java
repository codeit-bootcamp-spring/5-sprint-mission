package com.codeit.mission.discodeit.service;

import com.codeit.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.codeit.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.codeit.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatusResponse create(ReadStatusCreateRequest request);

    ReadStatusResponse find(UUID readStatusId);

    List<ReadStatusResponse> findAllByUserId(UUID userId);

    ReadStatusResponse update(ReadStatusUpdateRequest request);

    void delete(UUID readStatusId);
}
