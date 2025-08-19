package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class FileMessageService implements MessageService {

    private final String DIRECTORY;
    private final String EXTENSION;

    public FileMessageService() {
        this.DIRECTORY = "USER";
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
    public Message createMsg(String content, String channelId, String authorId) {
        Message message = new Message(content, channelId, authorId);
        Path path = Paths.get(DIRECTORY, message.getId() + EXTENSION);
        try(FileOutputStream fos = new FileOutputStream(path.toFile());
        ObjectOutputStream oos = new ObjectOutputStream(fos)){
            oos.writeObject(message);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
        return message;
    }

    @Override
    public Message find(UUID messageId) {
        Message message = null;
        Path path = Paths.get(DIRECTORY, messageId.toString() + EXTENSION);
        try(FileInputStream fis = new FileInputStream(path.toFile());
        ObjectInputStream ois = new ObjectInputStream(fis)){
            message = (Message) ois.readObject();
        }catch (Exception e) {
            throw new NoSuchElementException("존재하지 않는 메시지입니다.");
        }
        return message;
    }

    @Override
    public List<Message> findAll() {
        Path DIRECTORY = Paths.get("USER");
        try {
            return Files.list(DIRECTORY)
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
    public Message update(UUID messageId, String content, String channelId, String authorId) {
        Path path = Paths.get(DIRECTORY, messageId.toString() + EXTENSION);
        Message message = find(messageId);
        message.update(content,channelId,authorId);

        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException("메시지 정보 업데이트 실패", e);
        }

        return message;
    }

    @Override
    public Message delete(UUID userId) {
        Path path = Paths.get(DIRECTORY, userId.toString() + EXTENSION);
        Message message = find(userId);
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return message;
    }
}
