package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

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
  public User findById(UUID userId) {
    User found = data.get(userId); //UUID 키로 Map에서 유저 조회
    if (found == null) {
      return null;
    }
    return new User(found); // 복사본 반환
  }

  @Override
  public List<User> findAll() {//저장된 전체 유저 조회
    return List.copyOf(data.values()); // 불변 리스트로 반환
  }

  @Override
  public void update(User user) {
    save(user);
  }

  @Override
  public void delete(UUID userId) {
    data.remove(userId);
    saveToFile();

  }

  @Override
  public boolean existsByUserId(String userId) { //같은 userId를 가진 유저가 있는지 검사
    return data.values().stream()
        .anyMatch(user -> user.getUserId().equals(userId));
  }

  @Override
  public boolean existsByEmail(String email) { ////같은 email를 가진 유저가 있는지 검사
    return data.values().stream()
        .anyMatch(user -> user.getEmail().equals(email));
  }

  @Override
  public Optional<User> findByUserIdAndPassword(String userId, String password) {
    return data.values().stream()
        .filter(user -> user.getUserId().equals(userId) && user.getPassword().equals(password))
        .findFirst()
        .map(User::new); // 복사본 리턴
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
    if (!file.exists()) {
      return new HashMap<>();
    }

    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
      return (Map<UUID, User>) ois.readObject();
    } catch (IOException | ClassNotFoundException e) {
      throw new RuntimeException("유저 로딩 실패", e);
    }
  }
}
