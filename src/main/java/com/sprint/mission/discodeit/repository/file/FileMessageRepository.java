package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.*;
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

    public static void main(String[] args) {

        MessageRepository fmr = new FileMessageRepository();

        User user3 = new User("sss@sss.com", "kim", "1133", "#5666", UserStatus.OFFLINE);
        User user4 = new User("ttt@ttk.com", "park", "1564", "#4786", UserStatus.DND);
        User user5 = new User("kyy@yyk.com", "elis", "1777", "#9876", UserStatus.IDLE);
        User user2 = new User("jjj@jjj.com", "john", "3454", "#3132", UserStatus.ONLINE);
        User user1 = new User("kkk@kkk.com", "james", "1234", "#4756", UserStatus.ONLINE);

        System.out.println("user 1: " + user1.toString());
        System.out.println("user 2: " + user2.toString());
        System.out.println("user 3: " + user3.toString());
        System.out.println("user 4: " + user4.toString());
        System.out.println("user 5: " + user5.toString());

        Channel chnl1 = new Channel(user1.getId(), "BE-SPRING", ChannelType.COMMON,false);
        Channel chnl2 = new Channel(user3.getId(), "Chickens", ChannelType.PERSONAL,false);
        Channel chnl3 = new Channel(user4.getId(), "Movies", ChannelType.PERSONAL,true);

        System.out.println("channel 1:\n" + chnl1.toString());
        System.out.println("\nchannel 2:\n" + chnl2.toString());
        System.out.println("\nchannel 3:\n" + chnl3.toString());

        System.out.println("---------------- message file save -------------------------");

        Message msg1 = fmr.save(new Message(chnl1.getId(), "안녕하세요, 여러분!", user3.getId(), false));
        Message msg2 = fmr.save(new Message(chnl1.getId(), "오늘 날씨 어때요?", user2.getId(), false));
        Message msg3 = fmr.save(new Message(chnl1.getId(), "나빠요.", user2.getId(), false));
        Message msg4 = fmr.save(new Message(chnl2.getId(), "저메추 부탁드립니다.", user5.getId(), true));
        Message msg5 = fmr.save(new Message(chnl2.getId(), "치킨이요.", user3.getId(), true));

        System.out.println("msg 1:" + msg1.toString());
        System.out.println("msg 2:" + msg2.toString());
        System.out.println("msg 3:" + msg3.toString());
        System.out.println("msg 4:" + msg4.toString());
        System.out.println("msg 5:" + msg5.toString());

        System.out.println("---------------- message file findById -------------------------");
        System.out.println("find msg3 : " + fmr.findById(msg3.getId()).toString());
//        System.out.println("find msg3 : " + fmr.findById(UUID.randomUUID()).toString());

        System.out.println("---------------- read all messages -------------------------");
        List<Message> messages = fmr.findAll();
        messages.forEach(System.out::println);

        System.out.println("---------------- count messages -------------------------");
        System.out.println("The number of messages : " + fmr.count());

        System.out.println("---------------- delete message -------------------------");
        Message delM4 = fmr.delete(msg4.getId());
        System.out.println("Deleted message4 : " + delM4.toString());
    }

    public FileMessageRepository() {
        this.DIRECTORY = "MESSAGE/MessageRepository";
        this.EXTENSION = ".ser";
        Path path = Paths.get(DIRECTORY);
        if(!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Message save(Message message) {
        Path path = Paths.get(DIRECTORY, message.getId() + EXTENSION);
        try (FileOutputStream fos = new FileOutputStream(path.toFile(), false);
             ObjectOutputStream oos = new ObjectOutputStream(fos);) {
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
        Path path = Paths.get(DIRECTORY, id + EXTENSION);
        try (FileInputStream fis = new FileInputStream(path.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis);) {
            message = (Message)  ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return Optional.ofNullable(message);
    }

    @Override
    public List<Message> findAll() {
        List<Message> messages = new ArrayList<>();
        File file = new File(DIRECTORY);
        File[] files = file.listFiles((dir, name) -> name.endsWith(EXTENSION));
        if(files!= null) {
            for (File file1 : files) {
                try (FileInputStream fis = new FileInputStream(file1);
                ObjectInputStream ois = new ObjectInputStream(fis);) {
                    Message message = (Message) ois.readObject();
                    messages.add(message);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
        return messages;
    }

    @Override
    public long count() {
        File file = new File(DIRECTORY);
        Long count = (long) file.listFiles((dir, name) -> name.endsWith(EXTENSION)).length;

        if(count != null) {
            return count;
        }

        return -1L;
    }

    @Override
    public Message delete(UUID id) {
        Path path = Paths.get(DIRECTORY, id + EXTENSION);
        Message message = null;

        try (FileInputStream fis = new FileInputStream(path.toFile());
        ObjectInputStream ois = new ObjectInputStream(fis);) {
            if(existsById(id)) {
                message = (Message) ois.readObject();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        try {
            Files.deleteIfExists(path);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return message;
    }

    @Override
    public boolean existsById(UUID id) {
        File file = new File(DIRECTORY,  id + EXTENSION);
        if(file.exists()) {
            return true;
        }
        return false;
    }
}
