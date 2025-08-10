package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
<<<<<<< HEAD
import java.util.*;
=======
import java.util.List;
import java.util.Optional;
import java.util.UUID;
>>>>>>> 717adae (feat: 초기 커밋)

public class FileMessageRepository implements MessageRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileMessageRepository() {
<<<<<<< HEAD
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "data", Message.class.getSimpleName());
        if (!Files.exists(DIRECTORY)) {
=======
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", Message.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
>>>>>>> 717adae (feat: 초기 커밋)
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

<<<<<<< HEAD
    private Path resolvePath(UUID messageId) {
        return DIRECTORY.resolve(messageId.toString() + EXTENSION);
=======
    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
>>>>>>> 717adae (feat: 초기 커밋)
    }

    @Override
    public Message save(Message message) {
        Path path = resolvePath(message.getId());
<<<<<<< HEAD
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
        ObjectOutputStream oos = new ObjectOutputStream(fos)) {
=======
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
>>>>>>> 717adae (feat: 초기 커밋)
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return message;
    }

    @Override
<<<<<<< HEAD
    public Optional<Message> find(UUID messageId) {
        Message messageNullable = null;
        Path path = resolvePath(messageId);
        if (Files.exists(path)) {
            try (FileInputStream fis = new FileInputStream(path.toFile());
                 ObjectInputStream ois = new ObjectInputStream(fis)) {
=======
    public Optional<Message> findById(UUID id) {
        Message messageNullable = null;
        Path path = resolvePath(id);
        if (Files.exists(path)) {
            try (
                    FileInputStream fis = new FileInputStream(path.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
>>>>>>> 717adae (feat: 초기 커밋)
                messageNullable = (Message) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return Optional.ofNullable(messageNullable);
    }

    @Override
    public List<Message> findAll() {
        try {
            return Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
<<<<<<< HEAD
                        try (FileInputStream fis = new FileInputStream(path.toFile());
                             ObjectInputStream ois = new ObjectInputStream(fis)) {
=======
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
>>>>>>> 717adae (feat: 초기 커밋)
                            return (Message) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
<<<<<<< HEAD
                    }).toList();
=======
                    })
                    .toList();
>>>>>>> 717adae (feat: 초기 커밋)
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
<<<<<<< HEAD
    public boolean existById(UUID messageId) {
        Path path = resolvePath(messageId);
=======
    public boolean existsById(UUID id) {
        Path path = resolvePath(id);
>>>>>>> 717adae (feat: 초기 커밋)
        return Files.exists(path);
    }

    @Override
<<<<<<< HEAD
    public void delete(UUID messageId) {
        Path path = resolvePath(messageId);
=======
    public void deleteById(UUID id) {
        Path path = resolvePath(id);
>>>>>>> 717adae (feat: 초기 커밋)
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
<<<<<<< HEAD


=======
>>>>>>> 717adae (feat: 초기 커밋)
}
