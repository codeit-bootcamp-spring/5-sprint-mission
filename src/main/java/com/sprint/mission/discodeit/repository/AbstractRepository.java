package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.domain.entity.AbstractEntity;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AbstractRepository<T extends AbstractEntity> {

  T save(T entity);

  List<T> saveAll(Collection<T> entities);

  Optional<T> findById(UUID id);

  Optional<T> findByIdIncludingDeleted(UUID id);

  boolean existsById(UUID id);

  T getOrThrow(UUID id);

  List<T> findAll();

  List<T> findAllIncludingDeleted();

  List<T> findAllDeleted();

  List<T> findAllByIds(Collection<UUID> ids);

  List<T> findAllByIdsIncludingDeleted(Collection<UUID> ids);

  boolean softDeleteById(UUID id);

  int softDeleteAllByIds(Collection<UUID> ids);

  boolean restoreById(UUID id);

  int restoreAllByIds(Collection<UUID> ids);

  boolean hardDeleteById(UUID id);

  int hardDeleteAllByIds(Collection<UUID> ids);

  int hardDeleteAllExpired(Instant now);

  long count();

  long countIncludingDeleted();
}
