package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class FileMessageRepository implements MessageRepository {

    private final Path directory = Paths.get(System.getProperty("user.dir"), "data", "message");

    public FileMessageRepository() {
        createDirectory();
    }

    private void createDirectory() {

        try{
         Files.createDirectories(directory);
        } catch (IOException e) {
            throw new RuntimeException("디렉토리 생성 실패 : " + directory, e);
        }

    }

    private Path getFilePath(UUID id) {
        return directory.resolve(id.toString().concat(".ser"));
    }

    @Override
    public Message save(Message message) {
        Path file = getFilePath(message.getId());

        try (FileOutputStream fos = new FileOutputStream(file.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos);) {
            oos.writeObject(message);
            return message;
        } catch (IOException e) {
            throw new RuntimeException("메세지 저장 실패 : " + file, e);
        }
    }

    @Override
    public Optional<Message> findById(UUID id) {
        Path file = getFilePath(id);

        if(Files.notExists(file)){
            return Optional.empty();
        }

        try (FileInputStream fis = new FileInputStream(file.toFile());
              ObjectInputStream ois = new ObjectInputStream(fis);) {
            Message message = (Message) ois.readObject();
            if (message != null) {
                return Optional.of(message);
            } else {
                return Optional.empty();
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("메세지 불러오기 실패 : " + file, e);
        }

    }

    @Override
    public List<Message> findAll() {

        try{
         return Files.list(directory).filter(path -> path.toString().endsWith(".ser")).map(path -> {
             try (FileInputStream fis = new FileInputStream(path.toFile());
                  ObjectInputStream ois = new ObjectInputStream(fis)) {
              return (Message) ois.readObject();
             } catch (IOException | ClassNotFoundException e) {
                 throw new RuntimeException("메세지 로딩 실패 : " + path, e);
             }
         }).collect(Collectors.toList());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public Message update(Message message) {
        return save(message);
    }

    @Override
    public Message delete(UUID id) {
        Optional<Message> message = this.findById(id);
        if(message.isEmpty()){
            return  null;
        }

        try{
            Files.delete(getFilePath(id));
        } catch (IOException e) {
            throw new RuntimeException("메세지 삭제 실패 : " + id, e);
        }
        return message.get();
    }
}
