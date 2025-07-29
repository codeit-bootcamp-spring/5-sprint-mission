package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.util.*;

public class FileMessageRepository implements MessageRepository {
    private final File file = new File("messages.ser");
    private Map<UUID, Message> storage = new HashMap<>();

    public FileMessageRepository() {
        load();
    }

    @Override
    public Message save(Message message) {
        message.updateTimestamp();
        storage.put(message.getId(), message);
        saveToFile();
        return message;
    }

    @Override
    public Message findById(UUID id) {
        return storage.get(id);
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void deleteById(UUID id) {
        storage.remove(id);
        saveToFile();
    }

    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(storage);
        } catch (IOException e) {
            throw new RuntimeException("메시지 파일 저장 실패", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void load() {
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            storage = (Map<UUID, Message>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("메시지 파일 로딩 실패", e);
        }
    }
}
