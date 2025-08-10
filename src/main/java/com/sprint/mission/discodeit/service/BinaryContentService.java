package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.service.dto.binary.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.service.dto.binary.BinaryContentResponse;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

    // create
    UUID create(BinaryContentCreateRequest request);

    // find
    BinaryContentResponse find(UUID id);

    // findAllById (레포지토리 시그니처에 맞춰 Collection 사용)
    List<BinaryContentResponse> findAllById(Collection<UUID> ids);

    // delete
    void delete(UUID id);
}


