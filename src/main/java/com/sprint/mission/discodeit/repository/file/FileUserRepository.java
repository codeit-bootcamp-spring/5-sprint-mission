package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

public class FileUserRepository implements UserRepository {

    private final Path DIRECTORY;
    private static final String EXTENSION = ".ser";

    // ✅ 파라미터 없는 생성자 (스프링이 바로 만들 수 있음)
    public FileUserRepository(String users) {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"),
                "file-data-map",
                User.class.getSimpleName());
        try {
            Files.createDirectories(DIRECTORY);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public User save(User user) {
        Path path = resolvePath(user.getId());
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            oos.writeObject(user);
            return user;
        } catch (IOException e) {
            throw new RuntimeException("User 저장 실패: " + user.getId(), e);
        }
    }

    @Override
    public Optional<User> findById(UUID id) {
        Path path = resolvePath(id);
        if (!Files.exists(path)) return Optional.empty();
        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(path.toFile()))) {
            return Optional.of((User) ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("User 로드 실패: " + id, e);
        }
    }

    @Override
    public List<User> findAll() {
        try (Stream<Path> files = Files.list(DIRECTORY)) {
            List<User> users = new ArrayList<>();
            files.filter(p -> p.toString().endsWith(EXTENSION)).forEach(p -> {
                try (ObjectInputStream ois =
                             new ObjectInputStream(new FileInputStream(p.toFile()))) {
                    users.add((User) ois.readObject());
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });
            return users;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existsById(UUID id) {
        return Files.exists(resolvePath(id));
    }

    @Override
    public boolean deleteById(UUID id) {
        try {
            return Files.deleteIfExists(resolvePath(id));
        } catch (IOException e) {
            throw new RuntimeException("User 삭제 실패: " + id, e);
        }
    }
    @Override
    public void deleteAll() {
        try (var files = Files.list(DIRECTORY)) {
            files.filter(p -> p.toString().endsWith(EXTENSION))
                    .forEach(p -> {
                        try { Files.deleteIfExists(p); }
                        catch (IOException e) { throw new RuntimeException(e); }
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
