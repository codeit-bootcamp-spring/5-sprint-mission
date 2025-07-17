package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public interface Service<T> {
  void reset();

  List<T> findAll();

  T findById(UUID id);

  void deleteById(UUID id);

  void update(UUID id, Consumer<T> updater);
}
