package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileMessageRepository implements MessageRepository {

    private final Path directory;

    public FileMessageRepository(Path directory) {
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

    public Message save(Message message) {
        Path messageDirectory = Path.of(directory.toString() + "/" + message.getId());
        if(Files.exists(messageDirectory)) {
            delete(message.getId());
        }
        try (FileOutputStream fos = new FileOutputStream(messageDirectory.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(message);
            return message;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
    public Optional<Message> delete(UUID id) {
        Path messageDirectory = Path.of(directory.toString() + "/" + id);
        Message message = searchById(id).orElse(null);
        if (Files.exists(messageDirectory)) {
            try {
                Files.delete(messageDirectory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return Optional.ofNullable(message);
    }

    @Override
    public void deleteAll() {
        for (Message message : load(directory).values()) {
            delete(message.getId());
        }
    }

    @Override
    public Optional<Message> searchById(UUID id) {
        return Optional.ofNullable(load(directory).get(id));
    }

    @Override
    public List<Message> searchByContent(String content) {
        List<Message> messages = new ArrayList<>();
        for (Message m : load(directory).values()) {
            if (m.getContent().contains(content)) {
                messages.add(m);
            }
        }
        return messages;
    }

    @Override
    public List<Message> searchBySenderId(UUID id) {
        List<Message> messages = new ArrayList<>();
        for (Message m : load(directory).values()) {
            if (id.equals(m.getSenderId())) {
                messages.add(m);
            }
        }
        return messages;
    }

    @Override
    public List<Message> searchAll() {
        return new ArrayList<>(load(directory).values());
    }
}
