package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class FileMessageService implements MessageService {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileMessageService() {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), Message.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id.toString() + EXTENSION);
    }

    @Override
    public Message create(String content, String userId, UUID channelId) {
        Message message = new Message(content, userId, channelId);
        Path path = resolvePath(message.getId());

        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException("메시지 저장 실패", e);
        }

        return message;
    }

    @Override
    public List<Message> getMessages() {
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
                            throw new RuntimeException("실패: " + path, e);
                        }
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("실패", e);
        }
    }

    @Override
    public String getMessageById(UUID id) {
        Path path = resolvePath(id);
        if (Files.exists(path)) {
            try (
                    FileInputStream fis = new FileInputStream(path.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
                Message message = (Message) ois.readObject();
                return message.getContent();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException("메시지 조회 실패", e);
            }
        }
        throw new NoSuchElementException("해당 ID의 메시지를 찾을 수 없습니다: " + id);
    }

    @Override
    public List<Message> getMessagesByChannel(UUID channelId) {
        return getMessages().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> getMessagesByUser(String userId) {
        return getMessages().stream()
                .filter(message -> message.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Message update(UUID messageId, String newContent) {
        Path path = resolvePath(messageId);
        if (!Files.exists(path)) {
            throw new NoSuchElementException("메시지가 존재하지 않습니다");
        }

        Message message;
        try (
                FileInputStream fis = new FileInputStream(path.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            message = (Message) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("실패");
        }

        message.update(newContent);

        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException("업데이트 실패");
        }

        return message;
    }

    @Override
    public void delete(UUID messageId) {
        Path path = resolvePath(messageId);
        if (Files.notExists(path)) {
            throw new NoSuchElementException("삭제할 메시지가 존재하지 않습니다");
        }

        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException("메시지 삭제 실패");
        }
    }
}
