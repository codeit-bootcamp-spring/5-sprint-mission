package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FileUserService implements UserService {

    private static final String FILE_PATH =
            System.getProperty("user.home") + "/Desktop/users.txt";

    private Map<UUID, User> loadData() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new HashMap<>();
        }


        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            Object obj = ois.readObject();
            if (obj instanceof Map) {
                return (Map<UUID, User>) obj;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return  new HashMap<>();
        }

        return new HashMap<>();
    }

    private void saveData(Map<UUID, User> data) {

        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH));
            oos.writeObject(data);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void create(User user) {
        Map<UUID , User> data = loadData();
        data.put(user.getId(), user);
        saveData(data);
    }

    @Override
    public User find(UUID id) {
        Map<UUID, User> data = loadData();
        return data.get(id);
    }

    @Override
    public ArrayList<User> allFind() {
        Map<UUID, User> data = loadData();
        return new ArrayList<>(data.values());
    }

    @Override
    public void update(UUID id, User user) {
        Map<UUID, User> data = loadData();
        if (data.containsKey(id)) {
            data.put(id, user);
            saveData(data);
        }
    }

    @Override
    public void delete(UUID id) {
        Map<UUID, User> data = loadData();
        if (data.remove(id) != null) {
            saveData(data);
        }
    }
}
