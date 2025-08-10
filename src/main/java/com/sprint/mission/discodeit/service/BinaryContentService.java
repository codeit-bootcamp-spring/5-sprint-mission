package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.response.BinaryContentResponse;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

    BinaryContentResponse create(BinaryContentCreateRequest request);
    BinaryContentResponse findById(UUID id);
    List<BinaryContentResponse> findAllByUserId(List<UUID> ids);

    void delete(UUID id);
}
