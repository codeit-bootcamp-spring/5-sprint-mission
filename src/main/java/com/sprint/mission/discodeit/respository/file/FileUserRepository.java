package com.sprint.mission.discodeit.respository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.respository.UserRepository;

import java.io.*;
import java.util.*;

public class FileUserRepository implements UserRepository {

    private final String FILE_PATH = "data/user.store";
    private final Map<UUID, User> userMap = new HashMap<>();

    public FileUserRepository() {
        loadFromFile();
    }

    @Override
    public User save(User user) {
        userMap.put(user.getId(), user);
        saveToFile();
        return user;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public User findById(UUID id) {
        return userMap.get(id);
    }

    @Override
    public User findByName(String name) {
        return userMap.values().stream()
                .filter(user -> user.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(userMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof Map) {
                userMap.putAll((Map<UUID, User>) obj);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
