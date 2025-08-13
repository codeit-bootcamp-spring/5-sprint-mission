package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.configuration.RepositoryProps;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.ThrowableIOException;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileUserStatusRepository implements UserStatusRepository {
    private final Path directory;
    private static final String EXTENSION = ".ser";
    private static final String DOMAIN_NAME = UserStatus.class.getSimpleName();

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
    }

    private Path resolvePath(UUID id) {
        return directory.resolve(id + EXTENSION);
    }

    @Override
    public UserStatus save(UserStatus userStatus) {
        Path path = resolvePath(userStatus.getId());
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(userStatus);
        } catch (IOException e) {
            throw new ThrowableIOException("저장 실패 : " + path, e);
        }
        return userStatus;
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        UserStatus userStatus = null;
        Path path = resolvePath(id);
        if (Files.exists(path)) {
            try (FileInputStream fis = new FileInputStream(path.toFile());
                 ObjectInputStream ois = new ObjectInputStream(fis)) {
                userStatus = (UserStatus) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new ThrowableIOException("불러오기 실패 : " + path, e);
            }
        }
        return Optional.ofNullable(userStatus);
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return findAll().stream()
                .filter(status -> status.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public List<UserStatus> findAll() {
        try {
            return Files.list(directory)
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
                    .toList();
        } catch (IOException e) {
            throw new ThrowableIOException("불러오기 실패 : " + directory, e);
        }
    }

    @Override
    public boolean existsById(UUID id) {
        Path path = resolvePath(id);
        return Files.exists(path);
    }

    @Override
    public void deleteById(UUID id) {
        Path path = resolvePath(id);
        try {
            Files.delete(path);
        } catch (Exception e) {
            throw new ThrowableIOException("삭제 실패 : " + path, e);
        }
    }
}
