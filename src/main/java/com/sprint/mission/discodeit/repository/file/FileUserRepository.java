package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileUserRepository implements UserRepository {

    private final Path directory;

    public FileUserRepository(Path directory) {
        this.directory = directory;
        initPath(directory);
    }

    private void initPath(Path directory) {
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public User save(User user) {
        Path userDirectory = Path.of(directory.toString() + "/" + user.getId());

        if (Files.exists(userDirectory)) {
            delete(user.getId());
        }
        try (FileOutputStream fos = new FileOutputStream(userDirectory.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    private Map<UUID, User> load(Path directory) {
        if (Files.exists(directory)) {
            try {
                List<User> users = Files.list(directory)
                        .map(path -> {
                            try (FileInputStream fis = new FileInputStream(path.toFile());
                                 ObjectInputStream ois = new ObjectInputStream(fis);) {
                                Object data = ois.readObject();
                                return (User) data;
                            } catch (IOException | ClassNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        }).toList();
                Map<UUID, User> userMap = new HashMap<>();
                users.forEach(u -> userMap.put(u.getId(), u));
                return userMap;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return new HashMap<>();
        }
    }

    @Override
    public Optional<User> delete(UUID id) {
        Path userDirectory = Path.of(directory.toString() + "/" + id);
        User user = searchById(id).orElse(null);
        if (Files.exists(userDirectory)) {
            try {
                Files.delete(userDirectory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return Optional.ofNullable(user);
    }

    @Override
    public void deleteAll() {
        for (User user : load(directory).values()) {
            delete(user.getId());
        }
    }

    @Override
    public Optional<User> searchById(UUID id) {
        return Optional.ofNullable(load(directory).get(id));
    }

    @Override
    public List<User> searchByName(String name) {
        List<User> users = new ArrayList<>();
        for (User user : load(directory).values()) {
            if (user.getName().contains(name)) {
                users.add(user);
            }
        }
        return users;
    }

    @Override
    public List<User> searchAll() {
        return new ArrayList<>(load(directory).values());
    }
}
