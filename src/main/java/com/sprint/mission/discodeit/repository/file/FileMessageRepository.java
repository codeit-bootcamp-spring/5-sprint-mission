package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class FileMessageRepository implements MessageRepository {
    private final String DIRECTORY;
    private final String EXTENSION;

    public FileMessageRepository() {
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
    public Message save(Message message) {
        Path path = Paths.get(DIRECTORY, message.getId() + EXTENSION);
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)){
            oos.writeObject(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        Message message = null;
        Path path = Paths.get(DIRECTORY, id.toString() + EXTENSION);
        try(FileInputStream fis = new FileInputStream(path.toFile());
            ObjectInputStream ois = new ObjectInputStream(fis)){
            message = (Message) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(message);
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
    public long count() {
        return 0;
    }

    @Override
    public Message delete(UUID id) {
            Path path = Paths.get(DIRECTORY, id.toString() + EXTENSION);

            // Optional을 사용해 유저 조회
            Optional<Message> optionalUser = findById(id);

            // 유저가 존재하지 않으면 예외 던짐
            Message message = optionalUser.orElseThrow(() -> new NoSuchElementException("삭제할 메시지가 존재하지 않습니다."));

            // 파일 삭제 시도
            try {
                Files.delete(path);
            } catch (IOException e) {
                throw new RuntimeException("메시지 삭제 실패", e);
            }

            return message;
        }

    @Override
    public boolean existsById(UUID id) {
        Path path = Paths.get(DIRECTORY, id.toString() + EXTENSION);
        if (path.toFile().exists()) {
            return true;
        }
        return false;
    }
}
