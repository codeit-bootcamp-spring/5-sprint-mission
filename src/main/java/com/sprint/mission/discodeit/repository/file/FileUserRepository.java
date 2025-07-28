package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.util.*;

public class FileUserRepository implements UserRepository {
    private final File file = new File("users.ser");

    @Override
    public void save(User user) {
        Map<UUID, User> data = readFile();
        data.put(user.getId(), user);
        writeFile(data);
    }

    @Override
    public User findById(UUID id) {
        return readFile().get(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(readFile().values());
    }

    @Override
    public boolean update(UUID id, String newName) {
        Map<UUID, User> data = readFile();
        User user = data.get(id);
        if (user != null) {
            user.withName(newName);
            writeFile(data);
        }
        return false;
    }

    @Override
    public void delete(UUID id) {
        Map<UUID, User> data = readFile();
        data.remove(id);
        writeFile(data);
    }

    // 파일 초기화 (테스트용)
    // public void clearFile() {
    //     if (file.exists()) {
    //         if (file.delete()) {
    //             System.out.println("파일 초기화 완료: " + file.getName());
    //         } else {
    //             System.out.println("파일 초기화 실패");
    //         }
    //     }
    // }

    @SuppressWarnings("unchecked")
    private Map<UUID, User> readFile() {
        if (!file.exists()) return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof Map) {
                return (Map<UUID, User>) obj;
            } else {
                return new HashMap<>();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    private void writeFile(Map<UUID, User> data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

