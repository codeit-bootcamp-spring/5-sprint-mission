package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BaseService<T> {
  List<T> findAll();

  Optional<T> findById(UUID id);

  T getOrThrow(UUID id);

  T save(T entity);

  void deleteById(UUID id);
}
