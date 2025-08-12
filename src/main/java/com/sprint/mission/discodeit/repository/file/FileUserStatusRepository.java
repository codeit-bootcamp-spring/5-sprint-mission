package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUserStatusRepository implements UserStatusRepository {

    private final Path directory;
    private final String EXTENSION = ".ser";

    public FileUserStatusRepository(String folderName) {
        this.directory = Paths.get(System.getProperty("user.dir"), "file-data-map", folderName);
        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new RuntimeException("디렉토리 생성 실패", e);
        }
    }

    private Path resolvePath(UUID id) {
        return directory.resolve(id.toString() + EXTENSION);
    }

    @Override
    public UserStatus save(UserStatus status) {
        final Path path = resolvePath(status.getId());
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            oos.writeObject(status);
            return status;
        } catch (IOException e) {
            throw new RuntimeException("UserStatus 저장 실패: " + status.getId(), e);
        }
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        final Path path = resolvePath(id);
        if (!Files.exists(path)) return Optional.empty();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            return Optional.of((UserStatus) ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("UserStatus 조회 실패: " + id, e);
        }
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return findAll().stream()
                .filter(status -> status.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public boolean deleteByUserId(UUID userId) {
        Optional<UserStatus> target = findByUserId(userId);
        if (target.isEmpty()) return false;

        UUID id = target.get().getId();
        try {
            return Files.deleteIfExists(resolvePath(id));
        } catch (IOException e) {
            throw new RuntimeException("UserStatus 삭제 실패: " + id, e);
        }
    }

    @Override
    public List<UserStatus> findAll() {
        try (Stream<Path> files = Files.list(directory)) {
            return files
                    .filter(p -> p.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
                            return (UserStatus) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException("UserStatus 전체 조회 중 오류 발생", e);
                        }
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("UserStatus 파일 목록 조회 실패", e);
        }
    }
    @Override
    public boolean existsById(UUID id) {
        return Files.exists(resolvePath(id));
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return findAll().stream().anyMatch(status -> status.getUserId().equals(userId));
    }

    @Override
    public boolean deleteById(UUID id) {
        try {
            return Files.deleteIfExists(resolvePath(id));
        } catch (IOException e) {
            throw new RuntimeException("UserStatus 삭제 실패: " + id, e);
        }
    }
}
