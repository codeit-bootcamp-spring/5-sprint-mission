package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileMessageService implements MessageService {

    private final Path directory;

    public Path getDirectory() {
        return directory;
    }

    public FileMessageService(Path directory) {
        this.directory = directory;
        initPath(directory);
    }

    public void initPath(Path directory) {
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void save(Message message) {
        Path messageDirectory = Path.of(directory.toString() + "/" + message.getId());

        try (FileOutputStream fos = new FileOutputStream(messageDirectory.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Message> load(Path directory) {
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
    public void create(Message message) {
        if (!load(directory).contains(message)) {
            save(message);
        } else {
            System.out.println("이미 존재하는 메세지입니다.");
        }
    }

    @Override
    public void update(Message message) {
        delete(message);
        save(message);
    }

    @Override
    public void delete(Message message) {
        Path messageDirectory = Path.of(directory.toString() + "/" + message.getId());
        if (Files.exists(messageDirectory)) {
            try {
                Files.delete(messageDirectory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("존재하지 않는 메세지입니다.");
        }
    }

    @Override
    public Message searchByIndex(int i) {
        return null;
    }

    @Override
    public Message searchById(UUID id) {
        for (Message message : load(directory)) {
            if (message.getId().equals(id)) {
                return message;
            }
        }
        return null;
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
