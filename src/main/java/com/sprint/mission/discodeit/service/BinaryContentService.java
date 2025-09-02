package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentDTO;
import com.sprint.mission.discodeit.dto.request.binaryContent.UserProfileImageRequest;
import com.sprint.mission.discodeit.dto.response.binaryContent.BinaryContentResponse;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
	BinaryContentResponse create(UserProfileImageRequest request);
	BinaryContentResponse getById(UUID id);
	List<BinaryContentResponse> getAllByIdIn(List<UUID> ids);
	BinaryContentResponse delete(UUID id);
    BinaryContentDTO download(UUID id);
}