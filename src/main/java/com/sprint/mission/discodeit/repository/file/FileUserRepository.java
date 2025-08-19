package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.configuration.RepositoryProps;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ThrowableIOException;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileUserRepository implements UserRepository {

  private final Path directory;
  private static final String EXTENSION = ".ser";
  private static final String DOMAIN_NAME = User.class.getSimpleName();
  private final Map<UUID, User> userMap;

  public FileUserRepository(RepositoryProps props) {
    Path root = Paths.get(props.getFileDirectory());
    if (!root.isAbsolute()) {
      root = Paths.get(System.getProperty("user.dir")).resolve(root);
    }
    this.directory = root.resolve(DOMAIN_NAME);
    if (Files.notExists(directory)) {
      try {
        Files.createDirectories(directory);
      } catch (IOException e) {
        throw new ThrowableIOException("디렉토리 생성 실패: " + directory, e);
      }
    }
    userMap = new HashMap<>(load());
  }

  private Path resolvePath(UUID id) {
    return directory.resolve(id + EXTENSION);
  }

  private Map<UUID, User> load() {
    try (Stream<Path> paths = Files.list(directory)) {
      return paths
          .filter(path -> path.toString().endsWith(EXTENSION))
          .map(path -> {
            try (
                FileInputStream fis = new FileInputStream(path.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
              return (User) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
              throw new ThrowableIOException("불러오기 실패 : " + path, e);
            }
          })
          .collect(Collectors.toMap(User::getId, user -> user));
    } catch (IOException e) {
      throw new ThrowableIOException("불러오기 실패 : " + directory, e);
    }
  }

  @Override
  public User save(User user) {
    Path path = resolvePath(user.getId());
    try (
        FileOutputStream fos = new FileOutputStream(path.toFile());
        ObjectOutputStream oos = new ObjectOutputStream(fos)
    ) {
      oos.writeObject(user);
      userMap.put(user.getId(), user);
    } catch (IOException e) {
      throw new ThrowableIOException("저장 실패 :  " + path, e);
    }
    return user;
  }

  @Override
  public Optional<User> findById(UUID id) {
    return Optional.ofNullable(userMap.get(id));
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
    return List.copyOf(userMap.values());
  }

  @Override
  public boolean existsById(UUID id) {
    return userMap.containsKey(id);
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
    Path path = resolvePath(id);
    try {
      Files.deleteIfExists(path);
      userMap.remove(id);
    } catch (IOException e) {
      throw new ThrowableIOException("삭제 실패 : " + path, e);
    }
  }
}
