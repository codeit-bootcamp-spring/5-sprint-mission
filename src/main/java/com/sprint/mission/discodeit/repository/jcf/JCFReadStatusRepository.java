package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class JCFReadStatusRepository extends AbstractJCFRepository<ReadStatus> implements
    ReadStatusRepository {

  @Override
  public List<ReadStatus> findAllByUserId(UUID userId) {
    return data.values().stream()
        .filter(readStatus -> readStatus.getUserId().equals(userId))
        .toList();
  }
}
