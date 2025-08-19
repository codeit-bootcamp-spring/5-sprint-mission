package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContent create(@Valid BinaryContentCreateRequest binaryContentCreateRequest);

    BinaryContent findById(UUID id);

    List<BinaryContent> findAllByIdIn(List<UUID> ids);

    void delete(UUID id);
}
