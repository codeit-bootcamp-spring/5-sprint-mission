package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class FileMessageRepository implements MessageRepository {

    private final Path DIRECTORY_PATH = Paths.get("./data/messages");

    public FileMessageRepository() {
        if (Files.notExists(DIRECTORY_PATH)) {
            try {
                Files.createDirectories(DIRECTORY_PATH);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create directory: " + DIRECTORY_PATH, e);
            }
        }
    }

    private Path getMessageFilePath(UUID id) {
        return DIRECTORY_PATH.resolve(id + ".ser");
    }

    @Override
    public Message save(Message message) {
        try(FileOutputStream fos = new FileOutputStream(getMessageFilePath(message.getId()).toFile());
            ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(message);

        } catch (IOException e) {
            throw new RuntimeException("Failed to create message: " + message, e);
        }

        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        Message message;

        try (FileInputStream fis = new FileInputStream(getMessageFilePath(id).toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)
        ) {

            message = (Message) ois.readObject();

        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Message with id " + id + " not found");
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to read message: " + id, e);
        }

        return Optional.ofNullable(message);
    }

    @Override
    public List<Message> findAll() {
        try (Stream<Path> pathStream = Files.list(DIRECTORY_PATH)) {
            return pathStream
                    .map(path -> {
                        try (FileInputStream fis = new FileInputStream(path.toFile());
                             ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            Object data = ois.readObject();
                            return (Message) data;
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();

        } catch (IOException e) {
            throw new RuntimeException("Failed to read message", e);
        }
    }

    @Override
    public void deleteById(UUID id) {
        try {
            Files.delete(getMessageFilePath(id));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read message: " + id, e);
        }
    }
}
