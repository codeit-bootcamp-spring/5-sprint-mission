package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
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
public class JCFUserRepository implements UserRepository {

  Map<UUID, User> data = new HashMap<>();

  public JCFUserRepository() {
  }

  @Override
  public Optional<User> findByUserName(String username) {
    for (User user : data.values()) {
      if (user.getUserName().equals(username)) {
        return Optional.of(user);
      }
    }
    return Optional.empty();
  }

  @Override
  public Optional<User> findByEmail(String email) {
    for (User user : data.values()) {
      if (user.getEmail().equals(email)) {
        return Optional.of(user);
      }
    }
    return Optional.empty();
  }

  @Override
  public Optional<User> save(User user) {
    if (user == null) {
      return Optional.empty();
    }

    data.put(user.getId(), user);
    return Optional.of(user);
  }

  @Override
  public Optional<User> findById(UUID userId) {
    if (data.containsKey(userId)) {
      return Optional.of(data.get(userId));
    }
    return Optional.empty();
  }

  @Override
  public List<User> findAll() {
    return new ArrayList<>(data.values());
  }

  @Override
  public void delete(UUID userId) {
    data.remove(userId);
  }

  @Override
  public void deleteAll() {
    data.clear();
  }
}
