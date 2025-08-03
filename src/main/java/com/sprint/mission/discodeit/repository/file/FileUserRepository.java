package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

public class FileUserRepository implements UserRepository {
    private final Path userDirectory;

    public FileUserRepository() throws IOException {
        this.userDirectory = Path.of("data", "users");
        if (!Files.exists(userDirectory)) {
            Files.createDirectories(userDirectory);
        }
    }

    private Path getUserFile(UUID id) {
        return Path.of(userDirectory.toString(), id.toString() + ".ser");
    }

    @Override
    public void save(User user) throws IOException {
        Path filePath = getUserFile(user.getId());
        FileOutputStream fos = new FileOutputStream(filePath.toFile());
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(user);
        oos.close();
        fos.close();
    }

    @Override
    public User findById(UUID id) throws IOException, ClassNotFoundException {
        Path filePath = getUserFile(id);
        if (!Files.exists(filePath)) return null;

        FileInputStream fis = new FileInputStream(filePath.toFile());
        ObjectInputStream ois = new ObjectInputStream(fis);
        User user = (User) ois.readObject();
        ois.close();
        fis.close();
        return user;
    }

    @Override
    public User findByName(String name) throws IOException, ClassNotFoundException {
        try (Stream<Path> paths = Files.list(userDirectory)) {
            for (Path filePath : paths.toList()) {
                FileInputStream fis = new FileInputStream(filePath.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis);
                User user = (User) ois.readObject();
                ois.close();
                fis.close();
                if (user.getName().equals(name)) {
                    return user;
                }
            }
        }
        return null;
    }

    @Override
    public List<User> findAll() throws IOException, ClassNotFoundException {
        List<User> users = new ArrayList<>();
        try (Stream<Path> paths = Files.list(userDirectory)) {
            for (Path filePath : paths.toList()) {
                FileInputStream fis = new FileInputStream(filePath.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis);
                users.add((User) ois.readObject());
                ois.close();
                fis.close();
            }
        }
        return users;
    }

    @Override
    public void update(User user) throws IOException {
        save(user);
    }

    @Override
    public void delete(UUID id) throws IOException {
        Files.deleteIfExists(getUserFile(id));
    }
}
