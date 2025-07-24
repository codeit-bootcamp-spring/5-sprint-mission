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

    public void save(Message message) {
        Path messageDirectory = Path.of(directory.toString() + "/" + message.getId());

        try (FileOutputStream fos = new FileOutputStream(messageDirectory.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
    public void delete(Message message) {
        Path messageDirectory = Path.of(directory.toString() + "/" + message.getId());
        if (Files.exists(messageDirectory)) {
            try {
                Files.delete(messageDirectory);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.err.println("해당하는 메세지를 찾을 수 없습니다.");
            throw new NoSuchElementException();
        }
    }

    @Override
    public void deleteAll() {
        for (Message message : load(directory)) {
            delete(message);
        }
    }

    @Override
    public Optional<Message> searchById(UUID id) {
        Message message = null;
        for (Message m : load(directory)) {
            if (m.getId().equals(id)) {
                message = m;
            }
        }
        return Optional.ofNullable(message);
    }

    @Override
    public List<Message> searchByContent(String content) {
        List<Message> messages = new ArrayList<>();
        for (Message m : load(directory)) {
            if (m.getContent().contains(content)) {
                messages.add(m);
            }
        }
        return messages;
    }

    @Override
    public List<Message> searchBySenderId(UUID id) {
        List<Message> messages = new ArrayList<>();
        for (Message m : load(directory)) {
            if (id.equals(m.getSender())) {
                messages.add(m);
            }
        }
        return messages;
    }

    @Override
    public List<Message> searchAll() {
        return load(directory);
    }
}
