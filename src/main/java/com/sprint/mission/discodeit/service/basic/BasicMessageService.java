package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class BasicMessageService implements MessageService {

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;

    public static void main(String[] args) {
        UserRepository ur = new FileUserRepository();
        ChannelRepository cr = new FileChannelRepository();
        MessageService ms = new BasicMessageService(ur, cr);

        System.out.println("----------- create users ---------------");
        User user1 = ur.save(new User("kkk@kkk.com", "james", "1234", "#4756", UserStatus.ONLINE));
        User user2 = ur.save(new User("jjj@jjj.com", "john", "3454", "#3132", UserStatus.ONLINE));
        User user3 = ur.save(new User("sss@sss.com", "kim", "1133", "#5666", UserStatus.OFFLINE));
        User user4 = ur.save(new User("ttt@ttk.com", "park", "1564", "#4786", UserStatus.DND));
        User user5 = ur.save(new User("kyy@yyk.com", "elis", "1777", "#9876", UserStatus.IDLE));

        System.out.println("user 1: " + user1.toString());
        System.out.println("user 2: " + user2.toString());
        System.out.println("user 3: " + user3.toString());
        System.out.println("user 4: " + user4.toString());
        System.out.println("user 5: " + user5.toString());

        System.out.println("------------ create channels ---------------");
        Channel chnl1 = cr.save(new Channel(user1.getId(), "BE-SPRING", ChannelType.COMMON,false));
        Channel chnl2 = cr.save(new Channel(user3.getId(), "Chickens", ChannelType.PERSONAL,false));
        Channel chnl3 = cr.save(new Channel(user4.getId(), "Movies", ChannelType.PERSONAL,true));

        System.out.println("channel 1:\n" + chnl1.toString());
        System.out.println("\nchannel 2:\n" + chnl2.toString());
        System.out.println("\nchannel 3:\n" + chnl3.toString());

        System.out.println("------------ create messages ---------------");
        Message msg1 = ms.createMessage(chnl1, "안녕하세요, 여러분!", user3.getId(), false);
        Message msg2 = ms.createMessage(chnl1, "오늘 날씨 어때요?", user2.getId(), false);
        Message msg3 = ms.createMessage(chnl1, "나빠요.", user2.getId(), false);
        Message msg4 = ms.createMessage(chnl2, "저메추 부탁드립니다.", user5.getId(), true);
        Message msg5 = ms.createMessage(chnl2, "치킨이요.", user3.getId(), true);

        System.out.println("msg 1:" + msg1.toString());
        System.out.println("msg 2:" + msg2.toString());
        System.out.println("msg 3:" + msg3.toString());
        System.out.println("msg 4:" + msg4.toString());
        System.out.println("msg 5:" + msg5.toString());

        System.out.println("------------ find messages ---------------");
        Message findMsg2 = ms.findById(msg2.getId());
        System.out.println("find Message2 : "  + findMsg2.toString());

        System.out.println("------------ read all messages ---------------");
        List<Message> msgs = ms.findAll();
        msgs.forEach(System.out::println);

        System.out.println("------------ delete messages ---------------");
        Message delMsg4 = ms.deleteById(msg4.getId());
        System.out.println("delete Message4 : "  + delMsg4.toString());

        System.out.println("------------ update messages ---------------");
        System.out.println("before updated message : " + msg3.toString());
        Message updatedMsg3 = ms.update(msg3.getId(), "좋아요.", true);
        System.out.println("updated Message3 : " + updatedMsg3.toString());
        System.out.println("checking Message3 : " + ms.findById(msg3.getId()).toString());

    }

    public BasicMessageService(UserRepository userRepository, ChannelRepository channelRepository) {
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        messageRepository = new FileMessageRepository();
    }

    @Override
    public Message createMessage(Channel channel, String message, UUID author, boolean allMentioned) {
        if(!userRepository.existsById(author)) {
            throw new IllegalArgumentException("[Error] : 사용자가 존재하지 않습니다.");
        }
        if(!channelRepository.existsById(channel.getId())) {
            throw new IllegalArgumentException("[Error] : 채널이 존재하지 않습니다.");
        }
        return messageRepository.save(new Message(channel.getId(), message, author, allMentioned));
    }

    @Override
    public Message findById(UUID messageId) {
        Message findMessage = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("[Error]: id{" + messageId + "}는 존재하지 않는 사용자입니다."));

        return findMessage;
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
    public Message update(UUID messageId, String message, boolean allMentioned) {
        Message existMessage = findById(messageId);
        existMessage.update(message, allMentioned);

        return messageRepository.save(existMessage);
    }

    @Override
    public Message deleteById(UUID messageId) {
        return messageRepository.delete(messageId);
    }
}
