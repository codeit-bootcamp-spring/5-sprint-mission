package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.request.readStatus.CreateReadStatusRequest;
import com.sprint.mission.discodeit.dto.request.readStatus.UpdateReadStatusRequest;
import com.sprint.mission.discodeit.dto.response.readStatus.ReadStatusResponse;

public interface ReadStatusService {
	ReadStatusResponse create(CreateReadStatusRequest request);
	ReadStatusResponse getById(UUID id);
	List<ReadStatusResponse> getAllByUserId(UUID userId);
	List<ReadStatusResponse> getAllByChannelId(UUID channelId);
	ReadStatusResponse updateByChannelIdAndUserId(UUID channelId, UUID userId, UpdateReadStatusRequest request);
	ReadStatusResponse updateById(UUID id, UpdateReadStatusRequest request);
	ReadStatusResponse delete(UUID id);
}
