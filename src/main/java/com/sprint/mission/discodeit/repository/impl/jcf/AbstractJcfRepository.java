package com.sprint.mission.discodeit.repository.impl.jcf;

import com.sprint.mission.discodeit.domain.entity.AbstractEntity;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.repository.AbstractRepository;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AbstractJcfRepository<T extends AbstractEntity> implements AbstractRepository<T> {

  protected final ConcurrentMap<UUID, T> data = new ConcurrentHashMap<>();

  private final Class<T> entityType;

  @Override
  public T save(T entity) {
    Objects.requireNonNull(entity, "entity must not be null");
    data.put(entity.getId(), entity);
    return entity;
  }

  @Override
  public List<T> saveAll(Collection<T> entities) {
    if (entities == null || entities.isEmpty()) {
      return List.of();
    }
    for (T e : entities) {
      save(e);
    }
    return List.copyOf(entities);
  }

  @Override
  public Optional<T> findById(UUID id) {
    T e = data.get(id);
    return !e.isDeleted() ? Optional.of(e) : Optional.empty();
  }

  @Override
  public Optional<T> findByIdIncludingDeleted(UUID id) {
    return Optional.ofNullable(data.get(id));
  }

  @Override
  public T getOrThrow(UUID id) {
    return findById(id).orElseThrow(() ->
        new NotFoundException("%s with id %s not found".formatted(entityType.getSimpleName(), id)));
  }

  @Override
  public List<T> findAll() {
    return data.values().stream().filter(e -> !e.isDeleted()).toList();
  }

  @Override
  public List<T> findAllIncludingDeleted() {
    return List.copyOf(data.values());
  }

  @Override
  public List<T> findAllDeleted() {
    return data.values().stream().filter(AbstractEntity::isDeleted).toList();
  }

  @Override
  public List<T> findAllByIds(Set<UUID> ids) {
    if (ids == null || ids.isEmpty()) {
      return List.of();
    }
    return ids.stream()
        .map(data::get)
        .filter(e -> !e.isDeleted())
        .toList();
  }

  @Override
  public List<T> findAllByIdsIncludingDeleted(Set<UUID> ids) {
    if (ids == null || ids.isEmpty()) {
      return List.of();
    }
    return ids.stream()
        .map(data::get)
        .filter(Objects::nonNull)
        .toList();
  }

  @Override
  public boolean existsById(UUID id) {
    T e = data.get(id);
    return !e.isDeleted();
  }

  @Override
  public boolean softDeleteById(UUID id) {
    T entity = data.get(id);
    if (!entity.isDeleted()) {
      entity.delete();
      return true;
    }
    return false;
  }

  @Override
  public int softDeleteAllByIds(Set<UUID> ids) {
    if (ids == null || ids.isEmpty()) {
      return 0;
    }
    int count = 0;
    for (UUID id : ids) {
      if (softDeleteById(id)) {
        count++;
      }
    }
    return count;
  }

  @Override
  public boolean restoreById(UUID id) {
    T entity = data.get(id);
    if (entity != null && entity.isDeleted()) {
      entity.restore();
      return true;
    }
    return false;
  }

  @Override
  public int restoreAllByIds(Set<UUID> ids) {
    if (ids == null || ids.isEmpty()) {
      return 0;
    }
    int count = 0;
    for (UUID id : ids) {
      if (restoreById(id)) {
        count++;
      }
    }
    return count;
  }

  @Override
  public boolean hardDeleteById(UUID id) {
    return data.remove(id) != null;
  }

  @Override
  public int hardDeleteAllByIds(Set<UUID> ids) {
    if (ids == null || ids.isEmpty()) {
      return 0;
    }
    int count = 0;
    for (UUID id : ids) {
      if (hardDeleteById(id)) {
        count++;
      }
    }
    return count;
  }

  @Override
  public int hardDeleteAllExpired(Instant now) {
    Instant ref = (now != null) ? now : Instant.now();
    List<UUID> toRemove = data.values().stream()
        .filter(AbstractEntity::isDeleted)
        .filter(e -> e.shouldPurge(ref))
        .map(AbstractEntity::getId)
        .toList();
    for (UUID id : toRemove) {
      data.remove(id);
    }
    return toRemove.size();
  }

  @Override
  public long count() {
    return data.values().stream().filter(e -> !e.isDeleted()).count();
  }

  @Override
  public long countIncludingDeleted() {
    return data.size();
  }
}
