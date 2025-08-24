package com.sprint.mission.discodeit.repository.impl.jcf;

import com.sprint.mission.discodeit.domain.entity.AbstractEntity;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.repository.AbstractRepository;
import java.time.Instant;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
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

    entities.forEach(this::save);
    return List.copyOf(entities);
  }

  @Override
  public List<T> findAll() {
    return data.values().stream()
        .filter(AbstractEntity::isNotDeleted)
        .toList();
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
  public List<UUID> findAllIds() {
    return data.values().stream()
        .filter(AbstractEntity::isNotDeleted)
        .sorted(Comparator.comparing(AbstractEntity::getCreatedAt).reversed())
        .map(AbstractEntity::getId)
        .toList();
  }

  @Override
  public List<T> findAllByIdIn(Collection<UUID> ids) {
    return ids.stream()
        .map(data::get)
        .filter(Objects::nonNull)
        .filter(AbstractEntity::isNotDeleted)
        .sorted(Comparator.comparing(AbstractEntity::getCreatedAt).reversed())
        .toList();
  }

  @Override
  public List<T> findAllByIdIncludingDeleted(Set<UUID> ids) {
    if (ids == null || ids.isEmpty()) {
      return List.of();
    }
    return ids.stream()
        .map(data::get)
        .filter(Objects::nonNull)
        .toList();
  }

  @Override
  public Optional<T> findById(UUID id) {
    return Optional.ofNullable(data.get(id)).filter(AbstractEntity::isNotDeleted);
  }

  @Override
  public Optional<T> findByIdIncludingDeleted(UUID id) {
    return Optional.ofNullable(data.get(id));
  }

  @Override
  public Map<UUID, Instant> findAllCreatedAtById(Set<UUID> ids) {
    return data.values().stream()
        .filter(Objects::nonNull)
        .filter(AbstractEntity::isNotDeleted)
        .filter(e -> ids.contains(e.getId()))
        .collect(Collectors.toMap(
            AbstractEntity::getId,
            AbstractEntity::getCreatedAt
        ));
  }

  @Override
  public T getOrThrow(UUID id) {
    return findById(id).orElseThrow(() ->
        new NotFoundException("%s with id %s not found".formatted(entityType.getSimpleName(), id)));
  }

  @Override
  public boolean existsById(UUID id) {
    T e = data.get(id);
    return e != null && e.isNotDeleted();
  }

  @Override
  public boolean delete(UUID id) {
    T e = data.get(id);
    if (e != null && e.isNotDeleted()) {
      e.delete();
      return true;
    }
    return false;
  }

  @Override
  public void deleteAllByIdIn(Set<UUID> ids) {
    if (ids == null || ids.isEmpty()) {
      return;
    }
    ids.forEach(this::delete);
  }

  @Override
  public boolean restore(UUID id) {
    T entity = data.get(id);
    if (entity != null && entity.isDeleted()) {
      entity.restore();
      return true;
    }
    return false;
  }

  @Override
  public int restoreAllByIdIn(Set<UUID> ids) {
    if (ids == null || ids.isEmpty()) {
      return 0;
    }
    int count = 0;
    for (UUID id : ids) {
      if (restore(id)) {
        count++;
      }
    }
    return count;
  }

  @Override
  public boolean hardDelete(UUID id) {
    return data.remove(id) != null;
  }

  @Override
  public int hardDeleteAllByIdIn(Set<UUID> ids) {
    if (ids == null || ids.isEmpty()) {
      return 0;
    }
    int count = 0;
    for (UUID id : ids) {
      if (hardDelete(id)) {
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
    return data.values().stream().filter(AbstractEntity::isNotDeleted).count();
  }

  @Override
  public long countIncludingDeleted() {
    return data.size();
  }
}
