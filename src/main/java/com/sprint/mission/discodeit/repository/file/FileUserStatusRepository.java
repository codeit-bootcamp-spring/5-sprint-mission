package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class FileUserStatusRepository implements UserStatusRepository {

  private static final String FILE_PATH = "user-status.dat";
  private final Map<String, UserStatus> data = load(); // key = userId

  @Override
  public void update(UserStatus status) {
    data.put(status.getUserId().toString(), status);
    saveToFile();
  }

  @Override
  public UserStatus findByUserId(String userId) {
    return data.get(userId);
  }

  @Override
  public List<UserStatus> findAll() {
    return List.copyOf(data.values());
  }

  @Override
  public void save(UserStatus status) {
    if (status == null) {
      return;
    }
    // userId를 key로 해서 저장
    data.put(status.getUserId().toString(), status);
    saveToFile(); // 파일에 반영
  }


  @Override
  public void delete(UUID id) {
    if (id == null) {
      return;
    }
    data.values().removeIf(status -> status.getId().equals(id));
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
    if (!file.exists()) {
      return new HashMap<>();
    }

    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
      return (Map<String, UserStatus>) ois.readObject();
    } catch (IOException | ClassNotFoundException e) {
      throw new RuntimeException("UserStatus 로딩 실패", e);
    }
  }
}
