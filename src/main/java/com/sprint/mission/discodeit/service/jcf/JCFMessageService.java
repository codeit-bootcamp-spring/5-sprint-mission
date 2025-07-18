package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.MessageDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    List<Message> data = new ArrayList<>();

    public static void main(String[] args) {

        JCFMessageService jcfm = new JCFMessageService();
        JCFUserService jcfu = new JCFUserService();
        JCFChannelService jcfc = new JCFChannelService();

        System.out.println("------------ user create ------------");
        User user1 = jcfu.createUser("kkk@kkk.com", "james", "1234", "#4756", "online");
        User user2 = jcfu.createUser("jjj@jjj.com", "john", "3454", "#3132", "offline");
        User user3 = jcfu.createUser("sss@sss.com", "kim", "1133", "#5666", "online");
        User user4 = jcfu.createUser("ttt@ttk.com", "park", "1564", "#4786", "afk");
        User user5 = jcfu.createUser("kyy@yyk.com", "elis", "1777", "#9876", "good");

        System.out.println("user 1: " + user1.toString());
        System.out.println("user 2: " + user2.toString());
        System.out.println("user 3: " + user3.toString());
        System.out.println("user 4: " + user4.toString());
        System.out.println("user 5: " + user5.toString());

        try {
            System.out.println("------------- channel create ------------");
            Channel chnl1 = jcfc.createChannel(user1, "BE-SPRING", false);
            Channel chnl2 = jcfc.createChannel(user3, "Chickens", false);
            Channel chnl3 = jcfc.createChannel(user4, "Movies", true);

            System.out.println("channel 1:\n" + chnl1.toString());
            System.out.println("\nchannel 2:\n" + chnl2.toString());
            System.out.println("\nchannel 3:\n" + chnl3.toString());

            System.out.println("------------ add members  ------------");
            chnl1.addMembers(user2);
            List<User> members1 = chnl1.addMembers(user3);
            chnl2.addMembers(user2);
            List<User> members2 = chnl2.addMembers(user5);
            System.out.println("channel 1's Members : ");
            for(User u : members1) {
                System.out.println(u.toString());
            }
            System.out.println("channel 2's Members : ");
            for(User u : members2) {
                System.out.println(u.toString());
            }

            System.out.println("------------ Test Message  ------------");
            System.out.println("------------ create Message  ------------");
            Message msg1 = jcfm.createMessage(chnl1, "안녕하세요, 여러분!", user3, false);
            Message msg2 = jcfm.createMessage(chnl1, "오늘 날씨 어때요?", user2, false);
            Message msg3 = jcfm.createMessage(chnl1, "나빠요.", user2, false);
            Message msg4 = jcfm.createMessage(chnl2, "저메추 부탁드립니다.", user5, true);
            Message msg5 = jcfm.createMessage(chnl2, "치킨이요.", user3, true);
//            Message msg5 = jcfm.createMessage(chnl2, "", null, true);
//            Message msg1 = jcfm.createMessage(chnl1, "Hello, World!", user4, false);
            System.out.println("msg 1:" + msg1.toString());
            System.out.println("msg 2:" + msg2.toString());
            System.out.println("msg 3:" + msg3.toString());
            System.out.println("msg 4:" + msg4.toString());
            System.out.println("msg 5:" + msg5.toString());

            System.out.println("------------ find Message  ------------");
            Message findMsg1 = jcfm.findById(msg2.getId());
//            Message findMsg1 = jcfm.findById(null);
//            Message findMsg1 = jcfm.findById(UUID.randomUUID());
            System.out.println("find Message1 : "  + findMsg1.toString());
            System.out.println("------------ find All Message  ------------");
            List<Message> messages = jcfm.findAll();
            messages.forEach(System.out::println);
            System.out.println("------------ delete Message  ------------");
            Message delMsg1 = jcfm.deleteById(msg4.getId());
            System.out.println("delete Message1 : "  + delMsg1.toString());
//            Message delMsg2 = jcfm.deleteById(msg4.getId());
//            Message delMsg2 = jcfm.deleteById(null);
//            System.out.println("delete Message2 : "  + delMsg2.toString());
            System.out.println("------------ update Message  ------------");
            MessageDTO messageDTO1 = jcfm.createMessageDTO(msg3.getId(), msg3.getChannelId(), "좋아요.", msg3.getAuthor(), true);
            System.out.println("MessageDTO1 : " + messageDTO1.toString());
            System.out.println("before updated message : " + msg3.toString());
            Message updatedMsg1 = jcfm.update(messageDTO1);
            System.out.println("updated MessageDTO1 : " + updatedMsg1.toString());


        } catch (IllegalArgumentException | NullPointerException e) {
            System.out.println("[Error] : "+ e.getMessage());
        }

    }

    public JCFMessageService() {}

    @Override
    public Message createMessage(Channel channel, String message, User author, boolean allMentioned) throws NullPointerException, IllegalArgumentException {
        if (channel == null) {
            throw new NullPointerException("channel id is null.");
        } if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("message is null or empty.");
        } if (author == null) {
            throw new NullPointerException("author is null.");
        }
        List<User> members = channel.getMembers();
        if (! members.contains(author)) {
            throw new IllegalArgumentException("author is not in the channel.");
        }

        Message msg = new Message(channel.getId(), message, author, allMentioned);
        data.add(msg);

        return msg;
    }

    @Override
    public Message findById(UUID messageId) throws NullPointerException, IllegalArgumentException {
        if(messageId == null) {
            throw new NullPointerException("message id is null.");
        }
        for(Message msg : data) {
            if (msg.getId().equals(messageId)) {
                return msg;
            }
        }

        throw new IllegalArgumentException("message id not found.");
    }

    @Override
    public List<Message> findAll() {
        return data;
    }

    @Override
    public Message update(MessageDTO messageDTO) {
        if(messageDTO == null) {
            throw new  NullPointerException("messageDTO is null.");
        } if (messageDTO.getMessage() == null || messageDTO.getMessage().isBlank()) {
            throw new IllegalArgumentException("messageDTO message is null or blank.");
        }
        Iterator<Message> iter =  data.iterator();
        while(iter.hasNext()) {
            Message msg = iter.next();
            if (msg.getId().equals(messageDTO.getId())) {
                msg.update(messageDTO);
                return msg;
            }
        }
        throw new IllegalArgumentException("message not found.");
    }

    @Override
    public MessageDTO createMessageDTO(UUID messageId, UUID channelId, String message, User author, boolean allMentioned) throws NullPointerException, IllegalArgumentException {
        if (messageId == null) {
            throw new NullPointerException("message id is null.");
        } if (channelId == null) {
            throw new NullPointerException("channel id is null.");
        } if (message == null || message.isBlank()) {
            throw new  IllegalArgumentException("message is null or empty.");
        } if (author == null) {
            throw new  NullPointerException("author is null.");
        }

        return new MessageDTO(messageId, channelId, message, author, allMentioned);
    }

    @Override
    public Message deleteById(UUID messageId) {
        if(messageId == null) {
            throw new NullPointerException("message id is null.");
        }

        Iterator<Message> iter =  data.iterator();
        while(iter.hasNext()) {
            Message msg = iter.next();
            if (msg.getId().equals(messageId)) {
                data.remove(msg);
                return msg;
            }
        }

        throw new IllegalArgumentException("message is not found.");
    }
}
