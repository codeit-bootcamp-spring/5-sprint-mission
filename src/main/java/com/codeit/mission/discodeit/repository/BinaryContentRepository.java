package com.codeit.mission.discodeit.repository;

import com.codeit.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinaryContentRepository {
    BinaryContent save(BinaryContent binaryContent);

    Optional<BinaryContent> findById(UUID id);

    List<BinaryContent> findAllByIdIn(List<UUID> ids);

    boolean existsById(UUID id);

    void deleteById(UUID id);
}
