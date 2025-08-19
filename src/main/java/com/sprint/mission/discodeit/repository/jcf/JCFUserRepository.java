package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFUserRepository implements UserRepository {

  private final Map<UUID, User> userMap;

  public JCFUserRepository() {
    this.userMap = new HashMap<>();
  }

  @Override
  public User save(User user) {
    this.userMap.put(user.getId(), user);
    return user;
  }

  @Override
  public Optional<User> findById(UUID id) {
    return Optional.ofNullable(this.userMap.get(id));
  }

  @Override
  public Optional<User> findByUsername(String username) {
    User userNullable = userMap.values().stream()
        .filter(user -> user.getUsername().equals(username))
        .findFirst()
        .orElse(null);
    return Optional.ofNullable(userNullable);
  }

  @Override
  public List<User> findAll() {
    return this.userMap.values().stream().toList();
  }

  @Override
  public boolean existsById(UUID id) {
    return this.userMap.containsKey(id);
  }

  @Override
  public boolean existsByUsername(String username) {
    return userMap.values().stream().anyMatch(user -> user.getUsername().equals(username));
  }

  @Override
  public boolean existsByEmail(String email) {
    return userMap.values().stream().anyMatch(user -> user.getEmail().equals(email));
  }

  @Override
  public void deleteById(UUID id) {
    this.userMap.remove(id);
  }
}
