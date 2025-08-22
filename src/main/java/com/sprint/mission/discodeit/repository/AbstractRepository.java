package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.domain.entity.AbstractEntity;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
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

  List<T> findAllById(Set<UUID> ids);

  List<T> findAllByIdIncludingDeleted(Set<UUID> ids);

  Optional<T> findById(UUID id);

  Optional<T> findByIdIncludingDeleted(UUID id);

  T getOrThrow(UUID id);

  boolean existsById(UUID id);

  boolean softDeleteById(UUID id);

  void softDeleteAllById(Set<UUID> ids);

  boolean restoreById(UUID id);

  int restoreAllById(Set<UUID> ids);

  boolean hardDeleteById(UUID id);

  int hardDeleteAllById(Set<UUID> ids);

  int hardDeleteAllExpired(Instant now);

  long count();

  long countIncludingDeleted();
}
