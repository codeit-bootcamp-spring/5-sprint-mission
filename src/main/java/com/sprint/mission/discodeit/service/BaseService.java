package com.sprint.mission.discodeit.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BaseService<T> {
    List<T> findAll();

    List<T> findAllIncludingDeleted();

    Optional<T> findById(UUID id);

    List<T> findAllByIds(Collection<UUID> ids);

    boolean existsById(UUID id);

    T getOrThrow(UUID id);

    T save(T entity);

    boolean hardDeleteById(UUID id);

    boolean deleteById(UUID id);

    boolean restoreById(UUID id);

    long count();
}
