package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository
public class FileUserStatusRepository implements UserStatusRepository {

    private static final String FILE_PATH = "user-status.dat";
    private final Map<String, UserStatus> data = load(); // key = userId

    @Override
    public void save(UserStatus status) {
        data.put(status.getId().toString(), status);
        saveToFile();
    }

    @Override
    public UserStatus findByUserId(String userId) {
        return data.get(userId);
    }

    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void deleteByUserId(String userId) {
        data.remove(userId);
        saveToFile();
    }

    @Override
    public void delete(UUID id) {
        // ID로 해당 유저 상태를 찾아서 삭제
        String targetUserId = null;

        for (Map.Entry<String, UserStatus> entry : data.entrySet()) {
            if (entry.getValue().getId().equals(id)) {
                targetUserId = entry.getKey();
                break;
            }
        }

        if (targetUserId != null) {
            data.remove(targetUserId);
            saveToFile();
        }
    }

    @Override
    public void update(UserStatus status) {
        data.put(status.getId().toString(), status);
        saveToFile();
    }

    // 파일 저장
    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException("UserStatus 저장 실패", e);
        }
    }

    // 파일 불러오기
    private Map<String, UserStatus> load() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new HashMap<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<String, UserStatus>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("UserStatus 로딩 실패", e);
        }
    }
}
