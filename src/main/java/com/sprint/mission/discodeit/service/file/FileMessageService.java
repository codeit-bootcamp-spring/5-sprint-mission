package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class FileMessageService implements MessageService {
    private final String DIRECTORY;
    private final String EXTENSION;

    public FileMessageService() {
        this.DIRECTORY = "MESSAGE";
        this.EXTENSION = ".ser";
        Path path = Paths.get(DIRECTORY);
        if (!path.toFile().exists()) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Message create(String content, UUID channelId, UUID userId) {
        if (content == null || content.isBlank() || channelId == null || userId == null) {
            throw new IllegalArgumentException("Message info is invalid");
        }
        Message message = new Message(content, channelId, userId);
        Path path = Paths.get(DIRECTORY, message.getId() + EXTENSION);

        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return message;
    }

    @Override
    public Message find(UUID messageId) {
        Message message = null;
        Path path = Paths.get(DIRECTORY, messageId.toString() + EXTENSION);
        try (
                FileInputStream fis = new FileInputStream(path.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)) {
            message = (Message) ois.readObject();
        } catch (Exception e) {
            throw new NoSuchElementException("Message not found");
        }

        return message;
    }

    @Override
    public List<Message> findAll() {
        Path directory = Paths.get(DIRECTORY);
        try {
            return Files.list(directory)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            return (Message) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Message update(UUID messageId, String content) {
        Message oldMessage = null;
        Path path = Paths.get(DIRECTORY, messageId.toString() + EXTENSION);

        if (Files.exists(path)) {
            try (
                    FileInputStream fis = new FileInputStream(path.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
                oldMessage = (Message) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        Message message = Optional.ofNullable(oldMessage)
                .orElseThrow(() -> new NoSuchElementException("Message not found"));
        message.update(content);
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return message;
    }


    @Override
    public boolean delete(UUID messageId) {
        Path path = Paths.get(DIRECTORY, messageId.toString() + EXTENSION);

        if (Files.notExists(path)) {
            throw new NoSuchElementException("Message not found");
        }

        try {
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}