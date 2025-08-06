package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class FileUserRepository implements UserRepository {
    private final Path DIRECTORY;
    private final String EXTENSION;

    public FileUserRepository() {
        this.DIRECTORY = Path.of("USER");
        this.EXTENSION = ".ser";
        if (!DIRECTORY.toFile().exists()) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public User save(User user) {
        Path path = DIRECTORY.resolve(user.getId() + EXTENSION);
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(user);
            oos.flush();
            return user;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> findById(UUID id) {
        Path path = DIRECTORY.resolve(id + EXTENSION);
        try (FileInputStream fis = new FileInputStream(path.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            User user = (User) ois.readObject();
            return Optional.of(user);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<User> findAll() {
        if (Files.isDirectory(DIRECTORY)) {
            try {
                List<User> users = Files.list(DIRECTORY)
                        .filter(path -> path.toString().endsWith(EXTENSION))
                        .map(paths -> {
                                    try (FileInputStream fis = new FileInputStream(paths.toFile());
                                         ObjectInputStream ois = new ObjectInputStream(fis)) {
                                        User user = (User) ois.readObject();
                                        return user;
                                    } catch (IOException | ClassNotFoundException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                        )
                        .toList();
                return users;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return new ArrayList<>();
    }

    @Override
    public long count() {
        try (Stream<Path> stream = Files.list(DIRECTORY)) {
            return stream.filter(path -> (path.toString().endsWith(EXTENSION))).count();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User delete(UUID id) {
        Path path = DIRECTORY.resolve(id + EXTENSION);
        try {
            User user = findById(id).orElseThrow(() -> new RuntimeException("사용자에서 해당 " + id + "를 찾을 수 없습니다."));
            Files.deleteIfExists(path);
            return user;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean existsById(UUID id) {
        Path path = DIRECTORY.resolve(id + EXTENSION);
        return Files.exists(path);
    }
}
