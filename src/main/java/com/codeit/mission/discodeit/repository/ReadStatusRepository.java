package com.codeit.mission.discodeit.repository;

import com.codeit.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {
    ReadStatus save(ReadStatus readStatus);

    Optional<ReadStatus> findById(UUID id);

    List<ReadStatus> findAll();

    boolean existsById(UUID id);

    void deleteById(UUID id);
}
