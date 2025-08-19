package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.configuration.RepositoryProps;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.ThrowableIOException;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileMessageRepository implements MessageRepository {

  private final Path directory;
  private static final String EXTENSION = ".ser";
  private static final String DOMAIN_NAME = Message.class.getSimpleName();
  private final Map<UUID, Message> messageMap;

  public FileMessageRepository(RepositoryProps props) {
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
    messageMap = new HashMap<>(load());
  }

  private Path resolvePath(UUID id) {
    return directory.resolve(id + EXTENSION);
  }

  private Map<UUID, Message> load() {
    try (Stream<Path> paths = Files.list(directory)) {
      return paths
          .filter(path -> path.toString().endsWith(EXTENSION))
          .map(path -> {
            try (
                FileInputStream fis = new FileInputStream(path.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
              return (Message) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
              throw new ThrowableIOException("불러오기 실패 : " + path, e);
            }
          })
          .collect(Collectors.toMap(Message::getId, message -> message));
    } catch (IOException e) {
      throw new ThrowableIOException("불러오기 실패 : " + directory, e);
    }
  }

  @Override
  public Message save(Message message) {
    Path path = resolvePath(message.getId());
    try (
        FileOutputStream fos = new FileOutputStream(path.toFile());
        ObjectOutputStream oos = new ObjectOutputStream(fos)
    ) {
      oos.writeObject(message);
      messageMap.put(message.getId(), message);
    } catch (IOException e) {
      throw new ThrowableIOException("저장 실패 : " + path, e);
    }
    return message;
  }

  @Override
  public Optional<Message> findById(UUID id) {
    return Optional.ofNullable(messageMap.get(id));
  }

  @Override
  public List<Message> findAll() {
    return List.copyOf(messageMap.values());
  }

  @Override
  public boolean existsById(UUID id) {
    return messageMap.containsKey(id);
  }

  @Override
  public void deleteById(UUID id) {
    Path path = resolvePath(id);
    try {
      Files.deleteIfExists(path);
      messageMap.remove(id);
    } catch (IOException e) {
      throw new ThrowableIOException("삭제 실패 : " + path, e);
    }
  }
}
