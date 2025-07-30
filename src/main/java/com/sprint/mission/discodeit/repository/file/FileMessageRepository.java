package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
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


public class FileMessageRepository implements MessageRepository {
    private final String DIRECTORY;
    private final String EXTENSION;
    private final List<Message> data;



    public FileMessageRepository() {
        this.data=new ArrayList<>();
        this.DIRECTORY = "MESSAGE";
        this.EXTENSION = ".ser";
        Path path = Paths.get(DIRECTORY);
        if (!path.toFile().exists()) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }



    @Override
    public Message save(Message message) {
        Path path= Paths.get(DIRECTORY,message.getId()+EXTENSION);
        //File newFile=new File("/"+DIRECTORY+"/"+user.getId()+EXTENSION);
        try(FileOutputStream fos= new FileOutputStream(path.toFile());
            ObjectOutputStream oos =new ObjectOutputStream(fos)){
            oos.writeObject(message);
            data.add(message);
        }catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return message;
    }

    @Override
    public Optional<Message> findById(UUID messageId) {
        Message message= null;
        Path path= Paths.get(DIRECTORY,messageId.toString()+EXTENSION);
        try(FileInputStream fis = new FileInputStream(path.toFile());
            ObjectInputStream ois = new ObjectInputStream(fis); ){
            message=(Message)ois.readObject();
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return Optional.ofNullable(message);
    }

    @Override
    public List<Message> findAll() {
        List<Message> messages = new ArrayList<>();
        File folder = new File(DIRECTORY);

        // 디렉토리 내부의 모든 파일 반복
        File[] files = folder.listFiles((dir, name) -> name.endsWith(EXTENSION));
        if (files != null) {
            for (File file : files) {
                try (FileInputStream fis = new FileInputStream(file);
                     ObjectInputStream ois = new ObjectInputStream(fis)) {

                    Message message = (Message) ois.readObject();
                    messages.add(message);

                } catch (Exception e) {
                    e.printStackTrace(); // 문제 파일 하나는 무시하고 계속 진행
                    throw new RuntimeException(e);
                }
            }
        }
        return messages;
    }

    @Override
    public long count() {
        List<Message> messages = new ArrayList<>();
        File folder = new File(DIRECTORY);

        // 디렉토리 내부의 모든 파일 반복
        File[] files = folder.listFiles((dir, name) -> name.endsWith(EXTENSION));
        if (files != null) {
            for (File file : files) {
                try (FileInputStream fis = new FileInputStream(file);
                     ObjectInputStream ois = new ObjectInputStream(fis)) {

                    Message message = (Message) ois.readObject();
                    messages.add(message);

                } catch (Exception e) {
                    e.printStackTrace(); // 문제 파일 하나는 무시하고 계속 진행
                    throw new RuntimeException(e);
                }
            }
        }
        return messages.size();
    }

    @Override
    public Message delete(UUID messageId) {
        Path path = Paths.get(DIRECTORY, messageId.toString() + EXTENSION);
        File file = path.toFile();

        // 먼저 객체를 읽어옴
        Message message = null;
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {
                message = (Message) ois.readObject();
            } catch (Exception e) {
                throw new RuntimeException("삭제 전 사용자 로딩 실패: " + e.getMessage(), e);
            }
            // 그 다음 파일 삭제
            if (!file.delete()) {
                throw new RuntimeException("파일 삭제 실패: " + file.getAbsolutePath());
            }
        } else {
            throw new RuntimeException("삭제할 파일이 존재하지 않습니다: " + file.getAbsolutePath());
        }
        data.remove(message);
        return message;
    }

    @Override
    public boolean existsById(UUID messageId) {
        File folder = new File(DIRECTORY);
        // 디렉토리 내부의 모든 파일 반복
        File[] files = folder.listFiles((dir, name) -> name.endsWith(EXTENSION));
        if (files != null) {
            for (File file : files) {
                try (FileInputStream fis = new FileInputStream(file);
                     ObjectInputStream ois = new ObjectInputStream(fis)) {

                    Message message = (Message) ois.readObject();
                    if(message.getId().equals(messageId)){
                        return true;
                    }

                } catch (Exception e) {
                    e.printStackTrace(); // 문제 파일 하나는 무시하고 계속 진행
                    throw new RuntimeException(e);
                }
            }
        }
        return false;
    }
}
