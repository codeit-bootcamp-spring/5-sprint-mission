package com.sprint.mission.discodeit.repository.file;

<<<<<<< HEAD
import com.sprint.mission.discodeit.entity.Message;
=======
>>>>>>> 717adae (feat: 초기 커밋)
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
<<<<<<< HEAD
import java.util.*;
=======
import java.util.List;
import java.util.Optional;
import java.util.UUID;
>>>>>>> 717adae (feat: 초기 커밋)

public class FileUserRepository implements UserRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileUserRepository() {
<<<<<<< HEAD
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "data", User.class.getSimpleName());
        if (!Files.exists(DIRECTORY)) {
=======
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", User.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
>>>>>>> 717adae (feat: 초기 커밋)
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

<<<<<<< HEAD
    private Path resolvePath(UUID userId) {
        return DIRECTORY.resolve(userId.toString() + EXTENSION);
=======
    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
>>>>>>> 717adae (feat: 초기 커밋)
    }

    @Override
    public User save(User user) {
        Path path = resolvePath(user.getId());
<<<<<<< HEAD
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
        ObjectOutputStream oos = new ObjectOutputStream(fos)) {
=======
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
>>>>>>> 717adae (feat: 초기 커밋)
            oos.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    @Override
<<<<<<< HEAD
    public Optional<User> find(UUID userId) {
        User userNullable = null;
        Path path = resolvePath(userId);
        if (Files.exists(path)) {
            try(FileInputStream fis = new FileInputStream(path.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)) {
=======
    public Optional<User> findById(UUID id) {
        User userNullable = null;
        Path path = resolvePath(id);
        if (Files.exists(path)) {
            try (
                    FileInputStream fis = new FileInputStream(path.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
>>>>>>> 717adae (feat: 초기 커밋)
                userNullable = (User) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return Optional.ofNullable(userNullable);
    }

    @Override
    public List<User> findAll() {
        try {
            return Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
<<<<<<< HEAD
                        try (FileInputStream fis = new FileInputStream(path.toFile());
                             ObjectInputStream ois = new ObjectInputStream(fis)) {
=======
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
>>>>>>> 717adae (feat: 초기 커밋)
                            return (User) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
<<<<<<< HEAD
                    }).toList();
=======
                    })
                    .toList();
>>>>>>> 717adae (feat: 초기 커밋)
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
<<<<<<< HEAD
    public boolean existById(UUID userId) {
        Path path = resolvePath(userId);
=======
    public boolean existsById(UUID id) {
        Path path = resolvePath(id);
>>>>>>> 717adae (feat: 초기 커밋)
        return Files.exists(path);
    }

    @Override
<<<<<<< HEAD
    public void delete(UUID userId) {
        Path path = resolvePath(userId);
=======
    public void deleteById(UUID id) {
        Path path = resolvePath(id);
>>>>>>> 717adae (feat: 초기 커밋)
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
<<<<<<< HEAD

=======
>>>>>>> 717adae (feat: 초기 커밋)
}
