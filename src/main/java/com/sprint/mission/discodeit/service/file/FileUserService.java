package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;

public class FileUserService implements UserService {

    private final Path DIRECTORY_PATH = Paths.get("./data/users");

    public FileUserService() {
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
    public User create(String name, String email, String password) {
        User user = new User(
                name,
                email,
                password
        );

        try(FileOutputStream fos = new FileOutputStream(getUserFilePath(user.getId()).toFile());
            ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(user);
            return user;
        } catch (IOException e) {
            throw new RuntimeException("Failed to create user: " + user, e);
        }
    }

    @Override
    public User findById(UUID id) {
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

        return user;
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
    public synchronized User update(UUID id, String name, String email, String password) {
        File originalFile = getUserFilePath(id).toFile();
        File tempFile = new File(getUserFilePath(id) + ".tmp");

        User user;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(originalFile));
             ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(tempFile))
        ) {

            user = (User) ois.readObject();
            user.update(name, email, password);
            oos.writeObject(user);

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to read user: " + id, e);
        }

        if (!originalFile.delete() || !tempFile.renameTo(originalFile)) {
            throw new RuntimeException("Failed to replace user file after update");
        }

        return user;
    }

    @Override
    public void delete(UUID id) {
        try {
            Files.delete(getUserFilePath(id));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read user: " + id, e);
        }
    }
}
