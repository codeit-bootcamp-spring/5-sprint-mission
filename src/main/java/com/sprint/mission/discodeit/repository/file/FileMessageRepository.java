package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
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

@Repository // FileMessageRepository를 MessageRepository의 빈으로 등록해줌
public class FileMessageRepository implements MessageRepository {

  private final String FILE_PATH = "message.dat";
  private final Map<UUID, Message> data = load();

  @Override
  public void save(Message message) {
    data.put(message.getId(), message);
    saveToFile(); // 파일에 저장

  }

  @Override
  public Message findById(UUID messageId) {
    Message found = data.get(messageId);
    if (found == null) {
      return null;
    }
    return new Message(found); // 복사본 반환(생성자에 복사본 있음)
  }

  @Override
  public List<Message> findAll() {
    return List.copyOf(data.values());
  }

  @Override
  public void update(Message message) {
    save(message);

  }

  @Override
  public void delete(UUID id) {
    data.remove(id); // 메모리에서 삭제
    saveToFile();    // 파일에도 반영
  }

  @Override
  public void delete(Message message) {
    if (!data.containsKey(message.getId())) {
      throw new IllegalArgumentException("해당 ID의 메시지가 없습니다.");
    }
    data.remove(message.getId());
    saveToFile();
  }


  //객체 -> 파일 직렬화
  private void saveToFile() {
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
      oos.writeObject(data);
    } catch (IOException e) {
      throw new RuntimeException("메세지 저장 실패", e);
    }
  }


  //파일 -> 객체 역직렬화
  private Map<UUID, Message> load() {
    File file = new File(FILE_PATH);
    if (!file.exists()) {
      return new HashMap<>();
    }

    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
      return (Map<UUID, Message>) ois.readObject();
    } catch (IOException | ClassNotFoundException e) {
      throw new RuntimeException("메시지 로딩 실패", e);
    }
  }
}