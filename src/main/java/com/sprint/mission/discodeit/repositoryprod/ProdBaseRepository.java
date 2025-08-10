package com.sprint.mission.discodeit.repositoryprod;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

public interface ProdBaseRepository<T> {

    T save(T entity);

    List<T> saveAll(Collection<T> entities);

    Optional<T> findById(UUID id);

    List<T> findAll();

    List<T> findAllIncludingDeleted();

    List<T> findAllByIds(Set<UUID> ids);

    T getOrThrow(UUID id);

    boolean existsById(UUID id);

    boolean existsAllByIds(Set<UUID> ids);

    boolean deleteById(UUID id);

    int deleteAllByIds(Set<UUID> ids);

    boolean restoreById(UUID id);

    int restoreAllByIds(Set<UUID> ids);

    boolean hardDeleteById(UUID id);

    int hardDeleteAllByIds(Set<UUID> ids);

    long count();

    long count(Predicate<T> condition);
}
