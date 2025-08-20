package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.request.binaryContent.UserProfileImageRequest;
import com.sprint.mission.discodeit.dto.response.binaryContent.BinaryContentResponse;

public interface BinaryContentService {
	BinaryContentResponse create(UserProfileImageRequest request);
	BinaryContentResponse getById(UUID id);
	List<BinaryContentResponse> getAllByIdIn(List<UUID> ids);
	BinaryContentResponse delete(UUID id);
}