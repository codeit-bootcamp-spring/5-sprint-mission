package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.util.*;

public class FileUserRepository implements UserRepository {
    private final File file = new File("data.dir/users.ser");
    private final Map<UUID, User> data;

    public FileUserRepository() {
        this.data = readFromFile();
    }

    @Override
    public User save(User user) {
        data.put(user.getId(), user);
        writeToFile();
        return user;
    }

    @Override
    public User findById(UUID id) {
        return data.get(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void deleteById(UUID id) {
        data.remove(id);
        writeToFile();
    }

    @Override
    public void deleteAll() {
        data.clear();
        writeToFile();
    }

    private Map<UUID, User> readFromFile() {

        if (!file.exists()) {
            return new HashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new HashMap<>();
        }
    }

    private void writeToFile() {
        try {

            file.getParentFile().mkdirs();

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}