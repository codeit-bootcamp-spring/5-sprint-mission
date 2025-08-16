package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.BinaryContentResponse;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentResponse create(BinaryContentCreateRequest request);

    BinaryContentResponse find(UUID id);

    List<BinaryContentResponse> findAllByIdIn(List<UUID> ids);

    void delete(UUID id);

    void clear();
}