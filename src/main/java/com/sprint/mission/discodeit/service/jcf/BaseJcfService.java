package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.BaseEntity;
import com.sprint.mission.discodeit.service.BaseService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class BaseJcfService<T extends BaseEntity> implements BaseService<T> {
  protected final List<T> data = new ArrayList<>();

  private boolean idEquals(T entity, UUID id) {
    return entity != null && entity.getId() != null && entity.getId().equals(id);
  }

  private String getEntityName() {
    return getClass().getSimpleName().replace("Jcf", "").replace("Service", "");
  }

  protected void update(UUID id, Consumer<T> updater) {
    T entity = getOrThrow(id);
    updater.accept(entity);
    entity.touch();
  }

  @Override
  public List<T> findAll() {
    return data.stream().filter(e -> !e.isDeleted()).toList();
  }

  @Override
  public List<T> findAllIncludingDeleted() {
    return new ArrayList<>(data);
  }

  @Override
  public Optional<T> findById(UUID id) {
    return data.stream().filter(e -> !e.isDeleted() && idEquals(e, id)).findFirst();
  }

  @Override
  public List<T> findAllByIds(Collection<UUID> ids) {
    return data.stream()
        .filter(e -> !e.isDeleted() && ids.contains(e.getId()))
        .collect(Collectors.toList());
  }

  @Override
  public boolean existsById(UUID id) {
    return findById(id).isPresent();
  }

  @Override
  public T getOrThrow(UUID id) {
    return findById(id)
        .orElseThrow(
            () ->
                new NoSuchElementException(
                    String.format("엔티티(%s)를 찾을 수 없습니다: %s", getEntityName(), id)));
  }

  @Override
  public T save(T entity) {
    if (entity == null) {
      throw new IllegalArgumentException("엔티티는 null일 수 없습니다.");
    }

    data.removeIf(e -> idEquals(e, entity.getId()));
    data.add(entity);
    return entity;
  }

  @Override
  public boolean hardDeleteById(UUID id) {
    return data.removeIf(e -> idEquals(e, id));
  }

  @Override
  public boolean deleteById(UUID id) {
    Optional<T> target = data.stream().filter(e -> !e.isDeleted() && idEquals(e, id)).findFirst();
    if (target.isPresent()) {
      target.get().delete();
      return true;
    }
    return false;
  }

  @Override
  public boolean restoreById(UUID id) {
    Optional<T> target = data.stream().filter(e -> !e.isDeleted() && idEquals(e, id)).findFirst();
    target.ifPresent(BaseEntity::restore);
    return target.isPresent();
  }

  @Override
  public long count() {
    return data.stream().filter(e -> !e.isDeleted()).count();
  }
}
