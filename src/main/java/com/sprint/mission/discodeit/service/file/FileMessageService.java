package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;

public class FileMessageService implements MessageService {

    private final Path DIRECTORY_PATH = Paths.get("./data/messages");

    private final ChannelService channelService;
    private final UserService userService;

    public FileMessageService(ChannelService channelService, UserService userService) {
        if (Files.notExists(DIRECTORY_PATH)) {
            try {
                Files.createDirectories(DIRECTORY_PATH);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create directory: " + DIRECTORY_PATH, e);
            }
        }

        this.channelService = channelService;
        this.userService = userService;
    }

    private Path getMessageFilePath(UUID id) {
        return DIRECTORY_PATH.resolve(id + ".ser");
    }


    @Override
    public Message create(UUID authorId, UUID channelId, String content) {
        try {
            channelService.findById(channelId);
            userService.findById(authorId);
        } catch (NoSuchElementException e) {
            throw e;
        }

        Message message = new Message(authorId,
                channelId,
                content
        );

        try(FileOutputStream fos = new FileOutputStream(getMessageFilePath(message.getId()).toFile());
            ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(message);
            return message;
        } catch (IOException e) {
            throw new RuntimeException("Failed to create message: " + message, e);
        }
    }

    @Override
    public Message findById(UUID id) {
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

        return message;
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
    public Message update(UUID id, String content) {
        File originalFile = getMessageFilePath(id).toFile();
        File tempFile = new File(getMessageFilePath(id) + ".tmp");

        Message message;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(originalFile));
             ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(tempFile))
        ) {

            message = (Message) ois.readObject();
            message.update(content);
            oos.writeObject(message);

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to read message: " + id, e);
        }

        if (!originalFile.delete() || !tempFile.renameTo(originalFile)) {
            throw new RuntimeException("Failed to replace message file after update");
        }

        return message;
    }

    @Override
    public void delete(UUID id) {
        try {
            Files.delete(getMessageFilePath(id));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read message: " + id, e);
        }
    }
}
