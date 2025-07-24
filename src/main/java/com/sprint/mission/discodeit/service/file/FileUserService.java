package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileUserService implements UserService {

    private final Path directory;

    public Path getDirectory() {
        return directory;
    }

    public FileUserService(Path directory) {
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

    private void save(User user) {
        Path userDirectory = Path.of(directory.toString() + "/" + user.getId());

        try (FileOutputStream fos = new FileOutputStream(userDirectory.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<User> load(Path directory) {
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
    public void create(User user) {
        if (!load(directory).contains(user)) {
            save(user);
        } else {
            System.out.println("이미 존재하는 유저입니다.");
        }
    }

    @Override
    public void update(User user) {
        for (User u : load(directory)) {
            if (u.equals(user)) {
                System.out.println("변경 사항이 없습니다.");
                return;
            }
        }
        delete(user);
        save(user);
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
            System.out.println("존재하지 않는 유저입니다.");
        }
    }

    @Override
    public void deleteAll() {
        for (User user : load(directory)) {
            delete(user);
        }
    }

    @Override
    public User searchById(UUID id) {
        for (User user : load(directory)) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        return null;
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
