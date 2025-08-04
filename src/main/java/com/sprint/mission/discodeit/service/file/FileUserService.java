package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileUserService implements UserService {

    private final Path directory;

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

    private User save(User user) {
        Path userDirectory = Path.of(directory.toString() + "/" + user.getId());

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
    public User create(User user) {
        return save(user);
    }

    @Override
    public User updateName(UUID id, String name) {
        User user = searchById(id);
        user.updateName(name);
        return save(user);
    }

    @Override
    public User addChannel(UUID id, Channel channel) {
        User user = searchById(id);
        user.addChannel(channel);
        return save(user);
    }

    @Override
    public User deleteChannel(UUID id, Channel channel) {
        User user = searchById(id);
        user.deleteChannel(channel);
        return save(user);
    }


    @Override
    public User delete(UUID id) {
        Path userDirectory = Path.of(directory.toString() + "/" + id);
        User user = searchById(id);
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
        return user;
    }

    @Override
    public void deleteAll() {
        for (User user : load(directory).values()) {
            delete(user.getId());
        }
    }

    @Override
    public User searchById(UUID id) {
        User user = load(directory).getOrDefault(id, null);
        if (user == null) {
            System.err.println("해당하는 유저를 찾을 수 없습니다.");
            throw new NoSuchElementException();
        }
        return user;
    }

    @Override
    public List<User> searchByName(String name) {
        List<User> users = new ArrayList<>();

        for (User user : load(directory).values()) {
            if (user.getName().contains(name)) {
                users.add(user);
            }
        }
        if (users.isEmpty()) {
            System.err.println("해당하는 유저를 찾을 수 없습니다.");
            throw new NoSuchElementException();
        }
        return users;
    }

    @Override
    public List<User> searchAll() {
        return new ArrayList<>(load(directory).values());
    }
}
