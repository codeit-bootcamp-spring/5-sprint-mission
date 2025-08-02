package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileUserRepository implements UserRepository {

    private static final String USER_DATA_DIR = "user_data";

    public FileUserRepository() {
        File dataDir = new File(USER_DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }

    private String getUserFilePath(UUID userId) {
        return USER_DATA_DIR + File.separator + userId.toString() + ".ser";
    }

    @Override
    public User save(User user) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(getUserFilePath(user.getId())))) {
            oos.writeObject(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(getUserFilePath(id)))) {
            return Optional.of((User) ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        File dataDir = new File(USER_DATA_DIR);
        File[] userFiles = dataDir.listFiles((dir, name) -> name.endsWith(".ser"));
        if (userFiles != null) {
            for (File file : userFiles) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                    users.add((User) ois.readObject());
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return users;
    }

    @Override
    public void deleteById(UUID id) {
        File userFile = new File(getUserFilePath(id));
        if (userFile.exists()) {
            userFile.delete();
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return findAll().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }

    @Override
    public void clear() {
        File dataDir = new File(USER_DATA_DIR);
        File[] userFiles = dataDir.listFiles((dir, name) -> name.endsWith(".ser"));
        if (userFiles != null) {
            for (File file : userFiles) {
                file.delete();
            }
        }
    }
}
