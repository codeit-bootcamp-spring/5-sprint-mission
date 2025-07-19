package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

public interface Service<T> {
  void reset();

  T getIfExists(UUID id);

  List<T> findAll();

  T findById(UUID id);

  void deleteById(UUID id);
}
