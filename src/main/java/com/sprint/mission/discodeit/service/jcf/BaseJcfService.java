package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.BaseEntity;
import com.sprint.mission.discodeit.service.BaseService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public abstract class BaseJcfService<T extends BaseEntity> implements BaseService<T> {
  protected final List<T> data = new ArrayList<>();

  private boolean idEquals(T entity, UUID id) {
    return entity.getId().equals(id);
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
    return Collections.unmodifiableList(data);
  }

  @Override
  public Optional<T> findById(UUID id) {
    return data.stream().filter(e -> idEquals(e, id)).findFirst();
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
  public void deleteById(UUID id) {
    data.removeIf(e -> idEquals(e, id));
  }
}
