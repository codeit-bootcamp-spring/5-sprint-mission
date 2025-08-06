package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class FileUserRepository implements UserRepository {

    private final Path DIRECTORY_PATH = Paths.get("./data/users");

    public FileUserRepository() {
        if (Files.notExists(DIRECTORY_PATH)) {
            try {
                Files.createDirectories(DIRECTORY_PATH);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create directory: " + DIRECTORY_PATH, e);
            }
        }
    }

    private Path getUserFilePath(UUID id) {
        return DIRECTORY_PATH.resolve(id + ".ser");
    }

    @Override
    public User save(User user) {
        try(FileOutputStream fos = new FileOutputStream(getUserFilePath(user.getId()).toFile());
            ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(user);

        } catch (IOException e) {
            throw new RuntimeException("Failed to create user: " + user, e);
        }

        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        User user;

        try (FileInputStream fis = new FileInputStream(getUserFilePath(id).toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)
        ) {

            user = (User) ois.readObject();

        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("User with id " + id + " not found");
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to read user: " + id, e);
        }

        return Optional.ofNullable(user);
    }

    @Override
    public List<User> findAll() {
        try (Stream<Path> pathStream = Files.list(DIRECTORY_PATH)) {
            return pathStream
                    .map(path -> {
                        try (FileInputStream fis = new FileInputStream(path.toFile());
                             ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            Object data = ois.readObject();
                            return (User) data;
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();

        } catch (IOException e) {
            throw new RuntimeException("Failed to read users", e);
        }
    }

    @Override
    public void deleteById(UUID id) {
        try {
            Files.delete(getUserFilePath(id));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read user: " + id, e);
        }
    }
}
