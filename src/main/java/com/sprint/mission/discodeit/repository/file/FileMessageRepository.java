package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.util.*;

public class FileMessageRepository implements MessageRepository {

    private static final String FILE_PATH = "messages.ser";
    private final File file = new File(FILE_PATH);

    @Override
    public Message save(Message message) {
        Map<UUID, Message> data = readFile();
        data.put(message.getId(), message);
        writeFile(data);
        return message;
    }

    @Override
    public Message findById(UUID id) {
        Map<UUID, Message> data = readFile();
        return data.getOrDefault(id, null);
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(readFile().values());
    }

    @Override
    public Message update(UUID id, Message updatedMessage) {
        Map<UUID, Message> data = readFile();
        if (!data.containsKey(id)) {
            throw new NoSuchElementException("Message not found: " + id);
        }
        data.put(id, updatedMessage);
        writeFile(data);
        return updatedMessage;
    }

    @Override
    public Message update(UUID id, String newContent) {
        Map<UUID, Message> data = readFile();
        Message original = data.get(id);
        if (original == null) {
            return null;
        }
        Message updated = original.withContent(newContent);
        data.put(id, updated);
        writeFile(data);
        return updated;
    }

    @Override
    public void delete(UUID id) {
        Map<UUID, Message> data = readFile();
        data.remove(id);
        writeFile(data);
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, Message> readFile() {
        if (!file.exists()) {
            return new HashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof Map<?, ?> map) {
                return (Map<UUID, Message>) map;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[읽기 실패] " + e.getMessage());
        }

        return new HashMap<>();
    }

    private void writeFile(Map<UUID, Message> data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(data);
        } catch (IOException e) {
            System.err.println("[쓰기 실패] " + e.getMessage());
        }
    }

    public void clearFile() {
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("[파일 초기화 성공]");
            } else {
                System.err.println("[파일 초기화 실패]");
            }
        }
    }
}
