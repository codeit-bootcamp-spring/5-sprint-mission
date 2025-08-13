package com.codeit.mission.discodeit.service;

import com.codeit.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.codeit.mission.discodeit.dto.binarycontent.BinaryContentResponse;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentResponse create(BinaryContentCreateRequest request);

    BinaryContentResponse find(UUID binaryContentId);

    List<BinaryContentResponse> findAllByIdIn(List<UUID> ids);

    void delete(UUID binaryContentId);
}
