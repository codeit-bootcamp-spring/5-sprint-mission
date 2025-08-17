package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

    UUID create(BinaryContentCreateRequest request);

    BinaryContent findById(UUID id);

    List<BinaryContent> findAllByIdIn(List<UUID> ids);

    void deleteById(UUID id);
}
