package com.sprint.mission.discodeit.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BaseRepository<T> {
    T save(T entity);

    Optional<T> findById(UUID id);

    List<T> findAll();

    List<T> findAllIncludingDeleted();

    List<T> findAllByIds(Iterable<UUID> ids);

    boolean existsById(UUID id);

    boolean deleteById(UUID id);

    boolean hardDeleteById(UUID id);

    boolean restoreById(UUID id);

    long count();
}
