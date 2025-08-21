package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(
    name = "discodeit.repository.type",
    havingValue = "jcf",
    matchIfMissing = true
)
public class JCFUserStatusRepository implements UserStatusRepository {

  Map<UUID, UserStatus> data = new HashMap<>();

  @Override
  public Optional<UserStatus> save(UserStatus userStatus) {
    if (userStatus == null) {
      return Optional.empty();
    }
    data.put(userStatus.getId(), userStatus);
    return Optional.of(userStatus);
  }

  @Override
  public Optional<UserStatus> findById(UUID userStatusId) {
    if (data.containsKey(userStatusId)) {
      return Optional.of(data.get(userStatusId));
    }
    return Optional.empty();
  }

  @Override
  public Optional<UserStatus> findByUserId(UUID userId) {
    for (UserStatus userStatus : data.values()) {
      if (userStatus.getUserId().equals(userId)) {
        return Optional.of(userStatus);
      }
    }
    return Optional.empty();
  }

  @Override
  public void deleteByUserId(UUID userId) {
    data.values().removeIf(userStatus -> userStatus.getUserId().equals(userId));
  }

  @Override
  public void deleteAll() {
    data.clear();
  }

  @Override
  public List<UserStatus> findAll() {
    return new ArrayList<>(data.values());
  }

  @Override
  public void deleteById(UUID id) {
    data.remove(id);
  }
}
