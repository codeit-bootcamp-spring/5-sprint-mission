package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InvalidClassException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.io.WriteAbortedException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository("fileUserStatusRepository")
public class FileUserStatusRepository implements UserStatusRepository {

    private final Path DIRECTORY;
    private static final String EXTENSION = ".ser";

    public FileUserStatusRepository(@Value("${discodeit.repository.file-directory}") String fileDirectory) {
        this.DIRECTORY = Paths.get(fileDirectory).resolve(UserStatus.class.getSimpleName());
        try {
            if (Files.notExists(DIRECTORY)) {
                Files.createDirectories(DIRECTORY);
            }
        } catch (IOException e) {
            throw new RuntimeException("UserStatus 디렉토리 생성 실패", e);
        }
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id.toString() + EXTENSION);
    }

    @Override
    public UserStatus save(UserStatus userStatus) {
        Path path = resolvePath(userStatus.getId());
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(userStatus);
            oos.flush();
        } catch (IOException e) {
            throw new RuntimeException("UserStatus 삭제 실패.", e);
        }
        return userStatus;
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        Path path = resolvePath(id);
        if (Files.exists(path)) {
            return readFromFile(path);
        }
        return Optional.empty();
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        try {
            return Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(this::readFromFile)
                    .flatMap(Optional::stream)
                    .filter(userStatus -> userStatus.getUserId().equals(userId))
                    .findFirst();
        } catch (IOException e) {
            throw new RuntimeException("UserStatus 삭제 실패.", e);
        }
    }

    @Override
    public List<UserStatus> findAll() {
        try {
            return Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(this::readFromFile)
                    .flatMap(Optional::stream)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("UserStatus 삭제 실패.", e);
        }
    }

    @Override
    public boolean existsById(UUID id) {
        return Files.exists(resolvePath(id));
    }

    @Override
    public void deleteById(UUID id) {
        Path path = resolvePath(id);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("UserStatus 삭제 실패.", e);
        }
    }

    @Override
    public void deleteByUserId(UUID userId) {
        try {
            Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .forEach(path -> readFromFile(path).ifPresent(userStatus -> {
                        if (userStatus.getUserId().equals(userId)) {
                            try {
                                Files.deleteIfExists(path);
                            } catch (IOException e) {
                                throw new RuntimeException("UserStatus 삭제 실패.", e);
                            }
                        }
                    }));
        } catch (IOException e) {
            throw new RuntimeException("UserStatus 삭제 실패.", e);
        }
    }

    private Optional<UserStatus> readFromFile(Path path) {
        try (FileInputStream fis = new FileInputStream(path.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            Object obj = ois.readObject();
            if (obj instanceof UserStatus userStatus) {
                return Optional.of(userStatus);
            }
        } catch (WriteAbortedException | StreamCorruptedException | EOFException |
                 InvalidClassException | NotSerializableException e) {
            try {
                Files.deleteIfExists(path);
            } catch (IOException ignored) {
            }
        } catch (IOException | ClassNotFoundException e) {
        }
        return Optional.empty();
    }
}

