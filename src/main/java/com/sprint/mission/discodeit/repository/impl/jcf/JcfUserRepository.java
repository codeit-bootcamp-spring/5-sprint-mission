package com.sprint.mission.discodeit.repository.impl.jcf;

import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("test")
public class JcfUserRepository extends AbstractJcfRepository<User> implements UserRepository {

  public JcfUserRepository() {
    super(User.class);
  }

  @Override
  public Optional<User> findByUsername(String username) {
    return data.values().stream()
        .filter(User::isNotDeleted)
        .filter(u -> username.equals(u.getUsername()))
        .findFirst();
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return data.values().stream()
        .filter(User::isNotDeleted)
        .filter(u -> email.equals(u.getEmail()))
        .findFirst();
  }

  @Override
  public boolean existsByUsername(String username) {
    return findByUsername(username).isPresent();
  }

  @Override
  public boolean existsByEmail(String email) {
    return findByEmail(email).isPresent();
  }
}
