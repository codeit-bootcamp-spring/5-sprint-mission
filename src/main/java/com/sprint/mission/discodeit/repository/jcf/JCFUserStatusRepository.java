package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class JCFUserStatusRepository extends AbstractJCFRepository<UserStatus> implements
    UserStatusRepository {

  @Override
  public Optional<UserStatus> findByUserId(UUID id) {
    return data.values().stream()
        .filter(us -> us.getUserId().equals(id))
        .findFirst();
  }
}
