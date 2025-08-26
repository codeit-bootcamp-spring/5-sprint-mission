package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.domain.entity.AbstractEntity;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface AbstractRepository<T extends AbstractEntity> {

  T save(T entity);

  List<T> saveAll(Collection<T> entities);

  List<T> findAll();

  List<T> findAllIncludingDeleted();

  List<T> findAllDeleted();

  List<UUID> findAllIds();

  List<T> findAllByIdIn(Collection<UUID> ids);

  List<T> findAllByIdIncludingDeleted(Set<UUID> ids);

  Optional<T> find(UUID id);

  Optional<T> findIncludingDeleted(UUID id);

  Map<UUID, Instant> findAllCreatedAtById(Set<UUID> ids);

  T getOrThrow(UUID id);

  boolean existsById(UUID id);

  boolean delete(UUID id);

  void deleteAllByIdIn(Set<UUID> ids);

  boolean restore(UUID id);

  int restoreAllByIdIn(Set<UUID> ids);

  boolean hardDelete(UUID id);

  int hardDeleteAllByIdIn(Set<UUID> ids);

  int hardDeleteAllExpired(Instant now);

  long count();

  long countIncludingDeleted();
}
