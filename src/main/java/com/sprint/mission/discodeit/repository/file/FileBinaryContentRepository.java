package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.configuration.RepositoryProps;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.exception.ThrowableIOException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
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
public class FileBinaryContentRepository implements BinaryContentRepository {

  private final Path directory;
  private static final String EXTENSION = ".ser";
  private static final String DOMAIN_NAME = BinaryContent.class.getSimpleName();
  private final Map<UUID, BinaryContent> contentMap;

  public FileBinaryContentRepository(RepositoryProps props) {
    Path root = Paths.get(props.getFileDirectory());
    if (!root.isAbsolute()) {
      root = Paths.get(System.getProperty("user.dir")).resolve(root);
    }
    this.directory = root.resolve(DOMAIN_NAME);
    if (Files.notExists(directory)) {
      try {
        Files.createDirectories(directory);
      } catch (IOException e) {
        throw new ThrowableIOException("디렉토리 생성 실패 : " + directory, e);
      }
    }
    contentMap = new HashMap<>(load());
  }

  private Path resolvePath(UUID id) {
    return directory.resolve(id + EXTENSION);
  }

  private Map<UUID, BinaryContent> load() {
    try (Stream<Path> paths = Files.list(directory)) {
      return paths
          .filter(path -> path.toString().endsWith(EXTENSION))
          .map(path -> {
            try (
                FileInputStream fis = new FileInputStream(path.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
              return (BinaryContent) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
              throw new ThrowableIOException("불러오기 실패 : " + path, e);
            }
          })
          .collect(Collectors.toMap(BinaryContent::getId, binaryContent -> binaryContent));
    } catch (IOException e) {
      throw new ThrowableIOException("불러오기 실패 : " + directory, e);
    }
  }

  @Override
  public BinaryContent save(BinaryContent binaryContent) {
    Path path = resolvePath(binaryContent.getId());
    try (
        FileOutputStream fos = new FileOutputStream(path.toFile());
        ObjectOutputStream oos = new ObjectOutputStream(fos)
    ) {
      oos.writeObject(binaryContent);
      contentMap.put(binaryContent.getId(), binaryContent);
    } catch (IOException e) {
      throw new ThrowableIOException("저장 실패 : " + path, e);
    }
    return binaryContent;
  }

  @Override
  public Optional<BinaryContent> findById(UUID id) {
    return Optional.ofNullable(contentMap.get(id));
  }

  @Override
  public List<BinaryContent> findAll() {
    return List.copyOf(contentMap.values());
  }

  @Override
  public boolean existsById(UUID id) {
    return contentMap.containsKey(id);
  }

  @Override
  public void deleteById(UUID id) {
    Path path = resolvePath(id);
    try {
      Files.deleteIfExists(path);
      contentMap.remove(id);
    } catch (IOException e) {
      throw new ThrowableIOException("삭제 실패 : " + path, e);
    }
  }
}
