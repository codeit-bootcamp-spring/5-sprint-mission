package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class FileMessageRepository implements MessageRepository {
    private final Path DIRECTORY;
    private final String EXTENSION;


    public FileMessageRepository() {
        this.DIRECTORY = Path.of("MESSAGE");
        this.EXTENSION = ".ser";
        if (!DIRECTORY.toFile().exists()) {
            try {
                Files.createDirectory(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    @Override
    public Message save(Message message) {
        Path path = DIRECTORY.resolve(message.getId() + EXTENSION);
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(message);
            oos.flush();
            return message;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Message> findById(UUID id) {
        Path path = DIRECTORY.resolve(id + EXTENSION);
        try (FileInputStream fis = new FileInputStream(path.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            Message message = (Message) ois.readObject();
            return Optional.of(message);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Message> findAll() {
        if (Files.isDirectory(DIRECTORY)) {
            try {
                List<Message> messages = Files.list(DIRECTORY)
                        .filter(path -> path.toString().endsWith(EXTENSION))
                        .map(paths -> {
                                    try (FileInputStream fis = new FileInputStream(paths.toFile());
                                         ObjectInputStream ois = new ObjectInputStream(fis)) {
                                        Message message = (Message) ois.readObject();
                                        return message;
                                    } catch (IOException | ClassNotFoundException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                        )
                        .toList();
                return messages;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return new ArrayList<>();
    }

    @Override
    public long count() {
        try (Stream<Path> stream = Files.list(DIRECTORY)) {
            return stream.filter(path -> (path.toString().endsWith(EXTENSION))).count();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Message delete(UUID id) {
        Path path = DIRECTORY.resolve(id + EXTENSION);
        try {
            Message message = findById(id).orElseThrow(() -> new RuntimeException("메시지에서 해당 " + id + "를 찾을 수 없습니다."));
            Files.deleteIfExists(path);
            return message;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existsById(UUID id) {
        Path path = DIRECTORY.resolve(id + EXTENSION);
        return Files.exists(path);
    }
}
