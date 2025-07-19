package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.AbstractBaseEntity;
import com.sprint.mission.discodeit.service.Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public abstract class JcfService<T extends AbstractBaseEntity> implements Service<T> {
  protected final List<T> data = new ArrayList<>();

  protected boolean idEquals(T entity, UUID id) {
    return entity.getId().equals(id);
  }

  protected T requireEntity(UUID id) {
    T entity = findById(id);
    if (entity == null) {
      throw new NoSuchElementException("엔티티를 찾을 수 없습니다 : " + id);
    }
    return entity;
  }

  @Override
  public void reset() {
    data.clear();
  }

  @Override
  public List<T> findAll() {
    return Collections.unmodifiableList(data);
  }

  @Override
  public T findById(UUID id) {
    return data.stream().filter(e -> idEquals(e, id)).findFirst().orElse(null);
  }

  @Override
  public void deleteById(UUID id) {
    data.stream().filter(e -> idEquals(e, id)).findFirst().ifPresent(data::remove);
  }
}
