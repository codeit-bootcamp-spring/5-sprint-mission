package com.codeit.mission.discodeit.service;

import com.codeit.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.codeit.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContent create(BinaryContentCreateRequest request);

    BinaryContent find(UUID binaryContentId);

    List<BinaryContent> findAllByIdIn(List<UUID> binaryContentIds);

    void delete(UUID binaryContentId);
}
