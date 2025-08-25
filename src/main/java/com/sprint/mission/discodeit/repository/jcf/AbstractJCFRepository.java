package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BaseEntity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class AbstractJCFRepository<T extends BaseEntity> {

  protected final Map<UUID, T> data;

  protected AbstractJCFRepository() {
    this.data = new HashMap<>();
  }

  public T save(T entity) {
    data.put(entity.getId(), entity);
    return entity;
  }

  public Optional<T> findById(UUID id) {
    return Optional.of(data.get(id));
  }

  public List<T> findAll() {
    return new ArrayList<>(data.values());
  }

  public void deleteAll() {
    data.clear();
  }

  public void delete(UUID id) {
    data.remove(id);
  }
}