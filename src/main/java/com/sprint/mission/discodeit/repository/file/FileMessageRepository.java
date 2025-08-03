package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.*;
import java.io.*;
import java.nio.file.*;

public class FileMessageRepository implements MessageRepository{

    private final String DIRECTORY;
    private final String EXTENSION;

    public FileMessageRepository() {
        this.DIRECTORY = "MESSAGE";
        this.EXTENSION = ".ser";

        Path path = Paths.get(DIRECTORY);
        if(!path.toFile().exists()) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Message save(Message message) {
        Path path =  Paths.get(DIRECTORY, message.getId() + EXTENSION);

        try (FileOutputStream fos = new FileOutputStream(path.toFile());
            ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(message);
        } catch (Exception e) {
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
        }catch (IOException | ClassNotFoundException e){
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(message);
    }

    @Override
    public List<Message> findAll() {
        Path directory = Paths.get(DIRECTORY);

        if(Files.exists(directory)) {
            try {
                List<Message> messages = Files.list(directory)
                        .map(path -> {
                            try(
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                            ){
                                Object data = ois.readObject();
                                return (Message) data;
                            }catch (IOException | ClassNotFoundException e){
                                throw new RuntimeException(e);
                            }
                        }).toList();
                return messages;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            return new ArrayList<>();
        }
    }

    @Override
    public Message delete(UUID id) {
        Message message;
        Path path = Paths.get(DIRECTORY, id.toString() + EXTENSION);

        if(!Files.exists(path)) {
            throw new NoSuchElementException("Message with id" +  id + " does not exist");
        }
        // 메세지 읽어오기
        try (FileInputStream fis = new FileInputStream(path.toFile());
            ObjectInputStream ois = new ObjectInputStream(fis)) {
            message = (Message)ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 메세지 삭제
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return message;
    }


    @Override
    public boolean existById(UUID id) {
        Path path = Paths.get(DIRECTORY, id.toString() + EXTENSION);
        return Files.exists(path);
    }
}
