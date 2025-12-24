package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentDTO;
import com.sprint.mission.discodeit.dto.request.binaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.binaryContent.UserProfileImageRequest;
import com.sprint.mission.discodeit.dto.response.binaryContent.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentResponse create(UserProfileImageRequest request);

    BinaryContentResponse getById(UUID id) throws IOException;

    List<BinaryContentResponse> getAllByIdIn(List<UUID> ids) throws IOException;

    BinaryContentResponse delete(UUID id);

    BinaryContentDTO download(UUID id) throws IOException;

    BinaryContent createWithUserId(UUID authorId, BinaryContentCreateRequest attachment);
}