package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.readStatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.readStatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.readStatus.ReadStatusResponse;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatusResponse create(ReadStatusCreateRequest request);

    ReadStatusResponse getById(UUID id);

    List<ReadStatusResponse> getAllByUserId(UUID userId);

    List<ReadStatusResponse> getAllByChannelId(UUID channelId);

    ReadStatusResponse updateByChannelIdAndUserId(UUID channelId, UUID userId, ReadStatusUpdateRequest request);

    ReadStatusResponse updateById(UUID id, ReadStatusUpdateRequest request);

    ReadStatusResponse delete(UUID id);
}
