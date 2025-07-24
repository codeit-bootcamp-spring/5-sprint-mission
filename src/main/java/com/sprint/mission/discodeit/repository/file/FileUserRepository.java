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

    public void save(User user) {
        Path userDirectory = Path.of(directory.toString() + "/" + user.getId());

        try (FileOutputStream fos = new FileOutputStream(userDirectory.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<User> load(Path directory) {
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
                return users;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void delete(User user) {
        Path userDirectory = Path.of(directory.toString() + "/" + user.getId());
        if (Files.exists(userDirectory)) {
            try {
                Files.delete(userDirectory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.err.println("해당하는 유저를 찾을 수 없습니다.");
            throw new NoSuchElementException();
        }
    }

    @Override
    public void deleteAll() {
        for (User user : load(directory)) {
            delete(user);
        }
    }

    @Override
    public Optional<User> searchById(UUID id) {
        User user = null;
        for (User u : load(directory)) {
            if (u.getId().equals(id)) {
                user = u;
            }
        }
        return Optional.ofNullable(user);
    }

    @Override
    public List<User> searchByName(String name) {
        List<User> users = new ArrayList<>();
        for (User user : load(directory)) {
            if (user.getName().contains(name)) {
                users.add(user);
            }
        }
        return users;
    }

    @Override
    public List<User> searchAll() {
        return load(directory);
    }
}
