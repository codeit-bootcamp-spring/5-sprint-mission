package com.sprint.mission.discodeit.respository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.respository.MessageRepository;
import java.io.*;
import java.util.*;

public class FileMessageRepository implements MessageRepository {

    private final String FILE_PATH = "data/message.store";
    private Map<UUID, Message> messageMap = new HashMap<>();

    public FileMessageRepository() {
        loadFromFile();
    }

    @Override
    public Message save(Message message) {
        messageMap.put(message.getId(), message);
        saveToFile();
        return message;
    }

    @Override
    public Message findById(UUID id) {
        return messageMap.get(id);
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(messageMap.values());
    }

    @Override
    public List<Message> findByStr(String str) {
        List<Message> result = new ArrayList<>();
        for (Message message : messageMap.values()) {
            if (message.getContent().contains(str)) {
                result.add(message);
            }
        }
        return result;
    }

    @Override
    public boolean deleteById(UUID id) {
        boolean removed = messageMap.remove(id) != null;
        if (removed) {
            saveToFile();
        }
        return removed;
    }

    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(messageMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            Object obj = ois.readObject();
            if (obj instanceof Map) {
                messageMap = (Map<UUID, Message>) obj;
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
