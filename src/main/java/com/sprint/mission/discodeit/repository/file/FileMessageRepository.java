package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileMessageRepository implements MessageRepository {

    private final Path messageDirectory;

    public FileMessageRepository() throws IOException {
        messageDirectory = Path.of("data", "messages");
        if (!Files.exists(messageDirectory)) {
            Files.createDirectories(messageDirectory);
        }
    }

    private Path getMessageFile(UUID id) {
        return Path.of(messageDirectory.toString(), id.toString() + ".ser");
    }

    @Override
    public void save(Message message) throws IOException {
        Path filePath = getMessageFile(message.getId());
        FileOutputStream fos = new FileOutputStream(filePath.toFile());
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(message);
        oos.close();
        fos.close();
    }

    @Override
    public Message findById(UUID id) throws IOException, ClassNotFoundException {
        Path filePath = getMessageFile(id);
        if (!Files.exists(filePath)) return null;

        FileInputStream fis = new FileInputStream(filePath.toFile());
        ObjectInputStream ois = new ObjectInputStream(fis);

        Message message;
        message = (Message) ois.readObject();
        ois.close();
        fis.close();

        return message;
    }

    @Override
    public Message findByContent(String content) throws IOException, ClassNotFoundException {
        try (Stream<Path> paths = Files.list(messageDirectory)) {
            List<Path> pathList = paths.toList();
            for (Path path : pathList) {
                FileInputStream fis = new FileInputStream(path.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis);

                Message message;
                message = (Message) ois.readObject();

                ois.close();
                fis.close();

                if (message.getContent().equals(content)) {
                    return message;
                }
            }
        }
        return null;
    }

    @Override
    public List<Message> findAll() throws IOException, ClassNotFoundException {
        List<Message> messages = new ArrayList<>();

        try (Stream<Path> paths = Files.list(messageDirectory)) {
            List<Path> pathList = paths.toList();
            for (Path path : pathList) {
                FileInputStream fis = new FileInputStream(path.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis);
                 messages.add((Message) ois.readObject());
                 ois.close();
                 fis.close();

            }
        }

        return messages;
    }

    @Override
    public void update(Message message) throws IOException {
        delete(message.getId());
        save(message);
    }

    @Override
    public void delete(UUID id) throws IOException {
        Files.deleteIfExists(getMessageFile(id));
    }
}
