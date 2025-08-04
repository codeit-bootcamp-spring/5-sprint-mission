package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinaryContentService {

    void save(BinaryContent binaryContent);

    List<BinaryContent> findAll();

    Optional<BinaryContent> findById(UUID id);
}
