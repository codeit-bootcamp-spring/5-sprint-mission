package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileMessageRepository implements MessageRepository {
    private final String DIRECTORY;
    private final String EXTENSION;

    public FileMessageRepository(){
        DIRECTORY = "MESSAGE";
        EXTENSION = ".ser";
        Path path = Paths.get(DIRECTORY);
        if(!path.toFile().exists()){
            try {
                Files.createDirectory(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public Message save(Message message) {
        Path path = new File(DIRECTORY,message.getId()+EXTENSION).toPath();
        try (FileOutputStream fos = new FileOutputStream(path.toFile());
        ObjectOutputStream oos = new ObjectOutputStream(fos)){
            oos.writeObject(message);
            return message;
        } catch (Exception e) {
            e.printStackTrace();
            return null;

        }
    }

    @Override
    public Optional<Message> findById(UUID id) {
        Path path = Paths.get(DIRECTORY,id+EXTENSION);
        Optional<Message> message = Optional.empty();
        try (FileInputStream fis = new FileInputStream(path.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis);) {
            message = Optional.of((Message)ois.readObject());
            return message;
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<Message> findAll() {
        List<Message> messageList = new ArrayList<>();
        File file = new File(DIRECTORY);
        File[] folder = file.listFiles((dir,name)->name.endsWith(EXTENSION));
        if(folder==null){return messageList;}
        for(File f:folder) {
            try (FileInputStream fis = new FileInputStream(f);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {
                messageList.add((Message)ois.readObject());

            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        return messageList;
    }

    @Override
    public long count() {
        File folder = new File(DIRECTORY);
        File[] file = folder.listFiles((dir,name)-> name.endsWith(EXTENSION));
        if(file== null) return 0;

        return file.length;

    }

    @Override
    public boolean delete(UUID id) {
        File file = new File(DIRECTORY,id+EXTENSION);
        return file.delete();
    }

    @Override
    public boolean existsById(UUID id) {
        Path path = Paths.get(DIRECTORY,id+EXTENSION);
        return Files.exists(path);
    }

    @Override
    public boolean update(UUID messageId,String content) {
        File file = new File(DIRECTORY, messageId + EXTENSION);
        Message message;
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis);
        ) {
            message = (Message)ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        message.update(content);
        try (FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream oos = new ObjectOutputStream(fos);){
            oos.writeObject(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
