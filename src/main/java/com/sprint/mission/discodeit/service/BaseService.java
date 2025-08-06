package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface BaseService<T> {
    T save(T entity);

    Optional<T> findById(UUID id);

    List<T> findAll();

    List<T> findAllIncludingDeleted();

    List<T> findAllByIds(Set<UUID> ids);

    T getOrThrow(UUID id);

    boolean existsById(UUID id);

    boolean hardDeleteById(UUID id);

    boolean deleteById(UUID id);

    boolean restoreById(UUID id);

    long count();
}
