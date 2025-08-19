package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class FileUserStatusRepository extends AbstractFileRepository<UserStatus> implements
    UserStatusRepository {

  public FileUserStatusRepository() {
    super("data.dir", "userStatus");
  }

  public FileUserStatusRepository(String basePath) {
    super(basePath, "userStatus");
  }

  @Override
  public Optional<UserStatus> findByUserId(UUID id) {
    return data.values().stream()
        .filter(us -> us.getUserId().equals(id))
        .findFirst();
  }
}
