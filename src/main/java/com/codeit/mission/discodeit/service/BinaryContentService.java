package com.codeit.mission.discodeit.service;

import com.codeit.mission.discodeit.dto.data.BinaryContentDto;
import com.codeit.mission.discodeit.dto.request.BinaryContentCreateRequest;
import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

    BinaryContentDto create(BinaryContentCreateRequest request);

    BinaryContentDto find(UUID binaryContentId);

    List<BinaryContentDto> findAllByIdIn(List<UUID> binaryContentIds);

    void delete(UUID binaryContentId);
}
