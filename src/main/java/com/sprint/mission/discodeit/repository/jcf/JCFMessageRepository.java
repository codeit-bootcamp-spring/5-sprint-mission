package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.*;

public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> data;

    public static void main(String[] args) {
        MessageRepository mr = new JCFMessageRepository();

        User user1 = new User("kkk@kkk.com", "james", "1234", "#4756", UserStatus.ONLINE);
        User user2 = new User("jjj@jjj.com", "john", "3454", "#3132", UserStatus.ONLINE);
        User user3 = new User("sss@sss.com", "kim", "1133", "#5666", UserStatus.OFFLINE);
        User user4 = new User("ttt@ttk.com", "park", "1564", "#4786", UserStatus.DND);
        User user5 = new User("kyy@yyk.com", "elis", "1777", "#9876", UserStatus.IDLE);

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

        Message msg1 = new Message(chnl1.getId(), "안녕하세요, 여러분!", user3.getId(), false);
        Message msg2 = new Message(chnl1.getId(), "오늘 날씨 어때요?", user2.getId(), false);
        Message msg3 = new Message(chnl2.getId(), "나빠요.", user2.getId(), false);
        Message msg4 = new Message(chnl2.getId(), "저메추 부탁드립니다.", user5.getId(), true);
        Message msg5 = new Message(chnl3.getId(), "치킨이요.", user3.getId(), true);

        System.out.println("-------------- save messages ---------------");

        mr.save(msg1);
        mr.save(msg2);
        mr.save(msg3);
        mr.save(msg4);
        mr.save(msg5);

        System.out.println("msg 1:" + msg1.toString());
        System.out.println("msg 2:" + msg2.toString());
        System.out.println("msg 3:" + msg3.toString());
        System.out.println("msg 4:" + msg4.toString());
        System.out.println("msg 5:" + msg5.toString());

        System.out.println("-------------- read all messages ---------------");
        System.out.println("The number of messages : " + mr.count());
        System.out.println("Message List : ");
        List<Message> messages = mr.findAll();
        messages.forEach(System.out::println);

        System.out.println("-------------- find messages ---------------");
        Message findMsg2 = mr.findById(msg2.getId()).get();
        System.out.println("find Message2 : "  + findMsg2.toString());

        System.out.println("-------------- delete messages ---------------");
        Message deleteMsg = mr.delete(msg4.getId());
        System.out.println("delete Message4 : " + deleteMsg.toString());
    }

    public JCFMessageRepository() {
        data = new HashMap<>();
    }

    @Override
    public Message save(Message message) {
        data.putIfAbsent(message.getId(), message);

        return data.get(message.getId());
    }

    @Override
    public Optional<Message> findById(UUID id) {
        Message msg = data.get(id);

        return Optional.ofNullable(msg);
    }

    @Override
    public List<Message> findAll() {
        return data.values().stream().toList();
    }

    @Override
    public long count() {
        return (long) data.size();
    }

    @Override
    public Message delete(UUID id) {
        Message msg = null;

        if(existsById(id)) {
            msg = data.remove(id);
        }
        return msg;
    }

    @Override
    public boolean existsById(UUID id) {
        if(data.containsKey(id)) {
            return true;
        }
        return false;
    }
}
