package com.sprint.mission.discodeit.repository.impl.file;

import com.sprint.mission.discodeit.config.AppProperties;
import com.sprint.mission.discodeit.domain.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@Profile("dev")
public class FileUserRepository extends AbstractFileRepository<User> implements UserRepository {

  public FileUserRepository(AppProperties appProperties) {
    super(User.class, appProperties.storage());
  }

  @Override
  public Optional<User> findByUsername(String username) {
    try (Stream<Path> s = streamSerializedFiles()) {
      return s.map(this::readObject).flatMap(Optional::stream)
          .filter(User::isNotDeleted)
          .filter(u -> username.equals(u.getUsername()))
          .findFirst();
    } catch (IOException e) {
      log.warn("Failed to list saved files: {}", directory, e);
      throw new RuntimeException("Failed to list saved files: " + directory, e);
    }
  }


  @Override
  public Optional<User> findByEmail(String email) {
    try (Stream<Path> s = streamSerializedFiles()) {
      return s.map(this::readObject).flatMap(Optional::stream)
          .filter(User::isNotDeleted)
          .filter(u -> u.isNotDeleted() && email.equals(u.getEmail()))
          .findFirst();
    } catch (IOException e) {
      log.warn("Failed to list saved files: {}", directory, e);
      throw new RuntimeException("Failed to list saved files: " + directory, e);
    }
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
