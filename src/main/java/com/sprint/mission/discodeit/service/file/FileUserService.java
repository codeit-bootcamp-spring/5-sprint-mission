package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;

public class FileUserService implements UserService {

    private static final String USER_DATA_DIR = "user_data";

    public FileUserService() {
        File dataDir = new File(USER_DATA_DIR);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
    }

    private String getUserFilePath(UUID userId) {
        return USER_DATA_DIR + File.separator + userId.toString() + ".ser";
    }

    @Override
    public User create(String username, String password) {
        User user = new User(username, password);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(getUserFilePath(user.getId())))) {
            oos.writeObject(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public User find(UUID userId) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(getUserFilePath(userId)))) {
            return (User) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
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
    public User update(UUID id, String username, String password) {
        User user = find(id);
        if (user != null) {
            user.update(username, password);
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(getUserFilePath(user.getId())))) {
                oos.writeObject(user);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return user;
    }

    @Override
    public void delete(UUID id) {
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
