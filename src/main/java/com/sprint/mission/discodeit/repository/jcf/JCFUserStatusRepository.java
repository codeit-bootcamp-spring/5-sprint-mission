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
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFUserStatusRepository implements UserStatusRepository {

  private final Map<UUID, UserStatus> userStatusMap;

  public JCFUserStatusRepository() {
    userStatusMap = new HashMap<>();
  }

  @Override
  public UserStatus save(UserStatus userStatus) {
    userStatusMap.put(userStatus.getId(), userStatus);
    return userStatus;
  }

  @Override
  public Optional<UserStatus> findById(UUID id) {
    return Optional.ofNullable(userStatusMap.get(id));
  }

  @Override
  public Optional<UserStatus> findByUserId(UUID userId) {
    return userStatusMap.values().stream()
        .filter(u -> u.getUserId().equals(userId))
        .findFirst();
  }

  @Override
  public List<UserStatus> findAll() {
    return new ArrayList<>(userStatusMap.values());
  }

  @Override
  public boolean existsById(UUID id) {
    return userStatusMap.containsKey(id);
  }

  @Override
  public void deleteById(UUID id) {
    userStatusMap.remove(id);
  }
}
