package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.nio.file.Files.list;

public class FileMessageService implements MessageService {

    private final Path directory;

    public FileMessageService() {
        this.directory = Paths.get(System.getProperty("message.dir"), "message_data");
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(this.directory);
            }catch (IOException e){
                throw new RuntimeException("message_data 폴더 생성 실패", e);
            }
        }

    }

    public FileMessageService(UserService userService, ChannelService channelService) {
        this.directory = Paths.get(System.getProperty("message.dir"), "message_data");
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(this.directory);
            }catch (IOException e){
                throw new RuntimeException("message_data 폴더 생성 실패", e);
            }
        }
    }

    @Override
    public Message create(Message message) {
        if (message == null) {
            System.err.println("오류 : Message 생성에 실패. message가 null입니다.");
            return null;
        }
        Path filePath = this.directory.resolve(message.getMessageId() + ".ser");
        if (Files.exists(filePath)) {
            System.out.println("오류 : 이미 존재하는 Message ID입니다." + message.getMessageId());
            return null;
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toString()))) {
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException("Message 파일 생성 실패", e);
        }
        return message;
    }


    @Override
    public Optional<Message> findById(UUID messageId) {
        if (messageId == null) {
            System.out.println("오류 : findById에 실패. messageId가 null입니다.");
            return Optional.empty();
        }
        Path filePath = this.directory.resolve(messageId + ".ser");
        if(Files.exists(filePath)) {
            return Optional.empty();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toString()))) {
            Message message = (Message) ois.readObject();
            return  Optional.of(message);
        }
        catch (IOException | ClassNotFoundException e) {
            System.err.println("오류 : Message 역직렬화 실패 : (" + messageId + ")" + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<Message> findAll() {
        if (Files.exists(directory)) {
            return List.of();
        }
        try {
            List<Message> messageList = new ArrayList<>();
            list(directory).forEach(path -> {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))){
                    Message message = (Message) ois.readObject();
                    messageList.add(message);
                } catch (IOException | ClassNotFoundException e) {
                    System.err.println("오류: Message 파일 로딩 실패 (" + path.getFileName() + "): " + e.getMessage());
                }
            });
            return messageList;
        } catch (IOException e){
            throw new RuntimeException("message_data 폴더 목록 조회 실패", e);
        }
    }

    @Override
    public Optional<Message> update(UUID messageId, Message updatedMessage) {
        if (messageId == null || updatedMessage == null) {
            System.err.println("오류: update 실패. messageId 또는 updatedMessage가 null입니다.");
            return Optional.empty();
        }
        Path filePath = this.directory.resolve(messageId + ".ser");
        if(Files.exists(filePath)) {
            return Optional.empty();
        } try (ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(messageId);
        } catch (IOException e) {
            System.err.println("오류 : Message 업데이트 실패: "  + filePath + " / "+ e.getMessage());
            return Optional.empty();
        }
        System.out.println("messageId = " + messageId);
        return Optional.of(updatedMessage);
    }

    @Override
    public boolean delete(UUID messageId) {
        if (messageId == null) {
            System.err.println("오류: delete 실패. channelId가 null입니다.");
            return false;
        }
        Path filePath = this.directory.resolve(messageId + ".ser");
        try {
            boolean deletedMessageId = Files.deleteIfExists(filePath);
            if (deletedMessageId) {
                System.out.println("messageId 삭제 : " + messageId);
            } else {
                System.out.println("messageId 삭제 실패 : " + messageId + "파일이 존재 하지 않습니다.");
            }
            return deletedMessageId;

        } catch (IOException e) {
            System.err.println("오류 : Message 파일 삭제 실패: " + filePath + " / " + e.getMessage());
            return false;
        }
    }
}
