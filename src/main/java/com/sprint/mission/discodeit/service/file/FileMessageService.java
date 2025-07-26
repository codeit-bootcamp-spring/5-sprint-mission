package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

        try (FileOutputStream fos = new FileOutputStream(messageDirectory.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return message;
    }

    private List<Message> load(Path directory) {
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
                return messages;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public Message create(Message message) {
        return save(message);
    }

    @Override
    public Message update(Message message) {
        return save(message);
    }

    @Override
    public Message delete(UUID id) {
        Path messageDirectory = Path.of(directory.toString() + "/" + id);
        Message message = searchById(id).orElse(null);
        if (Files.exists(messageDirectory)) {
            try {
                Files.delete(messageDirectory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("존재하지 않는 메세지입니다.");
        }
        return message;
    }

    @Override
    public void deleteAll() {
        for (Message message : load(directory)) {
            delete(message.getId());
        }
    }

    @Override
    public Optional<Message> searchById(UUID id) {
        Message m = null;
        for (Message message : load(directory)) {
            if (message.getId().equals(id)) {
                m = message;
            }
        }
        return Optional.ofNullable(m);
    }

    @Override
    public List<Message> searchByContent(String content) {
        List<Message> messages = new ArrayList<>();
        for (Message message : load(directory)) {
            if (message.getContent().contains(content)) {
                messages.add(message);
            }
        }
        return messages;
    }

    @Override
    public List<Message> searchBySenderId(UUID id) {
        List<Message> messages = new ArrayList<>();

        for (Message message : load(directory)) {
            if (message.getSender().equals(id)) {
                messages.add(message);
            }
        }
        return messages;
    }

    @Override
    public List<Message> searchAll() {
        return load(directory);
    }
}