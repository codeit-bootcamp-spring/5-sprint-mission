package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

public interface BaseService<T> {
  List<T> findAll();

  T findById(UUID id);

  T getIfExists(UUID id);

  T save(T entity);

  void deleteById(UUID id);

  void reset();
}
