package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository // FileUserRepository를 UsserRepository 빈으로 등록
public class FileUserRepository implements UserRepository {

    private static final String FILE_PATH = "user.dat";
    private final Map<UUID, User> data = load(); //시작시 파일에서 로드

    @Override
    public void save(User user) {
        data.put(user.getId(), user);
        saveToFile();
    }

    @Override
    public User findById(UUID id) {
        User found = data.get(id);
        if (found == null) {
            return null;
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
        data.remove(id);
        saveToFile();

    }

    @Override
    public boolean existsByUserId(String userId) {
        return data.values().stream()
                .anyMatch(user -> user.getUserId().equals(userId));
    }

    @Override
    public boolean existsByEmail(String email) {
        return data.values().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }

    @Override
    public Optional<User> findOptionalById(UUID id) {
        return Optional.ofNullable(findById(id));
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

