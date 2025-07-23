package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;

public class FileUserService implements UserService {

    private final String FILE_PATH = "data/user.store";
    private Map<UUID, User> userMap = new HashMap<>();

    public FileUserService() {
        loadFromFile();
    }


    @Override
    public User create(String name, String password) {
        User user = new User(name, password);
        userMap.put(user.getId(), user);
        saveToFile();
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(userMap.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public User update(UUID id, String name) {
        User user = userMap.get(id);
        if (user != null) {
            user.updateName(name);
            saveToFile();
        }
        return user;
    }

    @Override
    public void delete(UUID id) {
        userMap.remove(id);
        saveToFile();
    }

    private void saveToFile() {
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs(); // 디렉토리 없으면 생성

        // 직렬화 방식
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(userMap);
        } catch (IOException e) {
            throw new RuntimeException("사용자 저장 중 오류 발생", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof Map) {
                userMap = (Map<UUID, User>) obj;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("사용자 불러오기 실패, 빈 상태로 초기화");
            userMap = new HashMap<>();
        }
    }


}
