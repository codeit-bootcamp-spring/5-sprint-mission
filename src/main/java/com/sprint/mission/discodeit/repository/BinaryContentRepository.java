package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.UUID;

public interface BinaryContentRepository {

    void save(BinaryContent binaryContent);

    BinaryContent findById(UUID id);

    List<BinaryContent> findAll();

    void deleteById(UUID id);
}
