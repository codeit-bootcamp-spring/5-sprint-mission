package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinaryContentRepository {

  void deleteById(UUID id);

  void deleteAll();

  Optional<BinaryContent> save(BinaryContent content);

  List<BinaryContent> findAll();

  Optional<BinaryContent> findById(UUID id);
}
