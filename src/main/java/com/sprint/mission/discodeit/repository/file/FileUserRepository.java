package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FileUserRepository implements UserRepository {
    private static final String FILE_PATH = System.getProperty("user.home") + "/Desktop/users.txt";

    @Override
    public void save(Map<UUID, User> data) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH));
            oos.writeObject(data);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public Map<UUID, User> loadData() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new HashMap<>();
        }

        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH));
            return (Map<UUID, User>) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }

    }

    @Override
    public void clear() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
    }
}
