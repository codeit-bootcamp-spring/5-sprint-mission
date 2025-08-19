package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class FileReadStatusRepository extends AbstractFileRepository<ReadStatus> implements
    ReadStatusRepository {

  public FileReadStatusRepository() {
    super("data.dir", "readStatus");
  }

  public FileReadStatusRepository(String basePath) {
    super(basePath, "readStatus");
  }

  @Override
  public List<ReadStatus> findAllByUserId(UUID userId) {

    return data.values().stream()
        .filter(readStatus -> readStatus.getUserId().equals(userId))
        .collect(Collectors.toList());
  }
}
