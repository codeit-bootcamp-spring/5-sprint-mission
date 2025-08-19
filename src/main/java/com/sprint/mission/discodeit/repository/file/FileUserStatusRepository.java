package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.configuration.RepositoryProps;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.ThrowableIOException;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
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
public class FileUserStatusRepository implements UserStatusRepository {
    private final Path directory;
    private static final String EXTENSION = ".ser";
    private static final String DOMAIN_NAME = UserStatus.class.getSimpleName();
    private final Map<UUID, UserStatus> userStatusMap;

    public FileUserStatusRepository(RepositoryProps props) {
        Path root = Paths.get(props.getFileDirectory());
        if (!root.isAbsolute()) {
            root = Paths.get(System.getProperty("user.dir")).resolve(root);
        }
        this.directory = root.resolve(DOMAIN_NAME);

        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new ThrowableIOException("디렉토리 생성 실패 : " + directory, e);
            }
        }
        userStatusMap = new HashMap<>(load());
    }

    private Path resolvePath(UUID id) {
        return directory.resolve(id + EXTENSION);
    }

    private Map<UUID, UserStatus> load() {
        try (Stream<Path> paths = Files.list(directory)) {
            return paths
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            return (UserStatus) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new ThrowableIOException("불러오기 실패 : " + path, e);
                        }
                    })
                    .collect(Collectors.toMap(UserStatus::getId, userStatus -> userStatus));
        } catch (IOException e) {
            throw new ThrowableIOException("불러오기 실패 : " + directory, e);
        }
    }

    @Override
    public UserStatus save(UserStatus userStatus) {
        Path path = resolvePath(userStatus.getId());
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(userStatus);
            userStatusMap.put(userStatus.getId(), userStatus);
        } catch (IOException e) {
            throw new ThrowableIOException("저장 실패 : " + path, e);
        }
        return userStatus;
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        return Optional.ofNullable(userStatusMap.get(id));
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return userStatusMap.values().stream()
                .filter(status -> status.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public List<UserStatus> findAll() {
        return List.copyOf(userStatusMap.values());
    }

    @Override
    public boolean existsById(UUID id) {
        return userStatusMap.containsKey(id);
    }

    @Override
    public void deleteById(UUID id) {
        Path path = resolvePath(id);
        try {
            Files.delete(path);
            userStatusMap.remove(id);
        } catch (Exception e) {
            throw new ThrowableIOException("삭제 실패 : " + path, e);
        }
    }
}
