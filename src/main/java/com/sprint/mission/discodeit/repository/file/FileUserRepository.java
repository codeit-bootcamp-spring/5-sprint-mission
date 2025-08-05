package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileUserRepository implements UserRepository {
    private final String DIRECTORY;
    private final String EXTENSION;

    public FileUserRepository() {
        this.DIRECTORY = "USER";
        this.EXTENSION = ".ser";
        Path path = Paths.get(DIRECTORY);
        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Path resolvePath(UUID id) {
        return Paths.get(DIRECTORY, id.toString() + EXTENSION);
    }

    @Override
    public User save(User user) {
        Path path = resolvePath(user.getId());
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save user: " + user.getId(), e);
        }
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        Path path = resolvePath(id);
        if (Files.exists(path)) {
            try (FileInputStream fis = new FileInputStream(path.toFile());
                 ObjectInputStream ois = new ObjectInputStream(fis)) {
                User user = (User) ois.readObject();
                return Optional.ofNullable(user);
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException("찾기 실패 ");
            }
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        try {
            return Files.list(Paths.get(DIRECTORY))
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (FileInputStream fis = new FileInputStream(path.toFile());
                             ObjectInputStream ois = new ObjectInputStream(fis)) {
                            return (User) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException("찾기 실패 ");
                        }
                    })
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("찾기 실패", e);
        }
    }

    @Override
    public void delete(UUID id) {
        Path path = resolvePath(id);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("삭제 실패");
        }
    }
}