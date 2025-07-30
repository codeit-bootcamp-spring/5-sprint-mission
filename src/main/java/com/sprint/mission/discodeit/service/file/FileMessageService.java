package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileMessageService implements MessageService {

    private final Path directory;

    public FileMessageService(Path directory) {
        this.directory = directory;
        initPath(directory);
    }

    private void initPath(Path directory) {
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Message save(Message message) {
        Path messageDirectory = Path.of(directory.toString() + "/" + message.getId());
        if (Files.exists(messageDirectory)) {
            delete(message.getId());
        }
        try (FileOutputStream fos = new FileOutputStream(messageDirectory.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return message;
    }

    private Map<UUID, Message> load(Path directory) {
        if (Files.exists(directory)) {
            try {
                List<Message> messages = Files.list(directory)
                        .map(path -> {
                            try (FileInputStream fis = new FileInputStream(path.toFile());
                                 ObjectInputStream ois = new ObjectInputStream(fis);) {
                                Object data = ois.readObject();
                                return (Message) data;
                            } catch (IOException | ClassNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        }).toList();
                Map<UUID, Message> messageMap = new HashMap<>();
                messages.forEach(m -> messageMap.put(m.getId(), m));
                return messageMap;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return new HashMap<>();
        }
    }

    @Override
    public Message create(Message message) {
        return save(message);
    }

    @Override
    public Message updateContent(UUID id, String content) {
        Message message = searchById(id);
        message.updateContent(content);
        return save(message);
    }

    @Override
    public Message updateSenderId(UUID id, UUID senderId) {
        Message message = searchById(id);
        message.updateSenderId(senderId);
        return save(message);
    }

    @Override
    public Message updateChannelId(UUID id, UUID channelId) {
        Message message = searchById(id);
        message.updateChannelId(channelId);
        return save(message);    }

    @Override
    public Message delete(UUID id) {
        Path messageDirectory = Path.of(directory.toString() + "/" + id);
        Message message = searchById(id);
        if (Files.exists(messageDirectory)) {
            try {
                Files.delete(messageDirectory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return message;
    }

    @Override
    public void deleteAll() {
        for (Message message : load(directory).values()) {
            delete(message.getId());
        }
    }

    @Override
    public Message searchById(UUID id) {
        Message message = load(directory).getOrDefault(id, null);
        if (message == null) {
            throw new NoSuchElementException("해당하는 메세지를 찾을 수 없습니다.");
        }
        return message;
    }

    @Override
    public List<Message> searchByContent(String content) {
        List<Message> messages = new ArrayList<>();
        for (Message message : load(directory).values()) {
            if (message.getContent().contains(content)) {
                messages.add(message);
            }
        }
        if (messages.isEmpty()) {
            throw new NoSuchElementException("해당하는 메세지를 찾을 수 없습니다.");
        }
        return messages;
    }

    @Override
    public List<Message> searchBySenderId(UUID id) {
        List<Message> messages = new ArrayList<>();

        for (Message message : load(directory).values()) {
            if (message.getSenderId().equals(id)) {
                messages.add(message);
            }
        }
        if (messages.isEmpty()) {
            throw new NoSuchElementException("해당하는 메세지를 찾을 수 없습니다.");
        }
        return messages;
    }

    @Override
    public List<Message> searchAll() {
        return new ArrayList<>(load(directory).values());
    }
}