package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.io.*;
import java.util.*;

public class FileMessageService implements MessageService {

    private final String FILE_PATH = "data/message.store";
    private Map<UUID, Message> messageMap = new HashMap<>();

    public FileMessageService() {
        loadFromFile();
    }

    @Override
    public Message create(User user, Channel channel, String content) {
        Message message = new Message(user, channel, content);
        messageMap.put(message.getId(), message);
        saveToFile();
        return message;
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(messageMap.values());
    }

    @Override
    public List<Message> findByStr(String str) {
        List<Message> messages = new ArrayList<>();
        for (Message message : messageMap.values()) {
            if (message.getContent().contains(str)) {
                messages.add(message);
            }
        }
        return messages;
    }

    @Override
    public Message update(UUID id, String newMessage) {
        Message message = messageMap.get(id);
        if (message != null) {
            message.updateContent(newMessage);
            saveToFile();
        }
        return message;
    }

    @Override
    public boolean deleteById(UUID id) {
        if (messageMap.remove(id) != null) {
            saveToFile();
            return true;
        }
        return false;
    }

    private void saveToFile() {
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs(); // 디렉토리 없으면 생성
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(messageMap);
        } catch (IOException e) {
            throw new RuntimeException("채널 저장 중 오류 발생", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof Map) {
                messageMap = (Map<UUID, Message>) obj;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("채널 불러오기 실패, 빈 상태로 초기화");
            messageMap = new HashMap<>();
        }
    }
}
