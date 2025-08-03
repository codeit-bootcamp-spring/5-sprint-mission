package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FileUserRepository implements UserRepository {

    private static final String FILE_PATH = "user.dat";
    private final Map<UUID, User> data = load(); //시작시 파일에서 로드

    @Override
    public void save(User user) {
        data.put(user.getId(), user);
    }

    @Override
    public User findById(UUID id) {
        User found = data.get(id);
        if (found == null) {
            throw new IllegalArgumentException("해당 ID의 유저가 없습니다.");
        }
        return new User(found); // 복사본 반환
    }

    @Override
    public List<User> findAll() {
        return List.copyOf(data.values()); // 불변 리스트로 반환
    }

    @Override
    public void update(User user) {
        save(user);
    }

    @Override
    public void delete(UUID id) {
        if (!data.containsKey(id)) {
            throw new IllegalArgumentException("해당 ID의 유저가 없습니다.");
        }
        data.remove(id);
        saveToFile();
    }

    //객체 -> 파일 직렬화
    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException("유저 저장 실패", e);
        }
    }

    //파일 -> 객체 역직렬화
    private Map<UUID, User> load() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new HashMap<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("유저 로딩 실패", e);
        }
    }
}

