package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.List;

public class JavaApplication {
    public static void main(String[] args) {
        testChannel();
        testUser();
        testMessage();
    }

    public static void testChannel() {
        System.out.println("--------------Channel--------------");
        JCFChannelService channelService = new JCFChannelService();

        System.out.println("1. 등록");
        Channel channel1 = channelService.createChannel("Hi", "안녕하세요");
        Channel channel2 = channelService.createChannel("Hello", "안녕");

        System.out.println("등록 완료! " + channel1);
        System.out.println("등록 완료! " + channel2);

        System.out.println("2. 보기");
        Channel channel = channelService.readChannel(channel1.getId());
        if (channel != null) {
            System.out.println(channel.getId() + " " + channel.getName() + " " + channel.getDescription());
        } else {
            System.out.println("존재하지 않는 회원입니다.");
        }


        System.out.println("3. 모두 보기");
        List<Channel> channelList = channelService.readAllChannels();
        if (channelList.isEmpty()) {
            System.out.println("존재하지 않습니다.");
        } else {
            for (Channel ch : channelList) {
                System.out.println(ch.getId() + " " + ch.getName() + " " + ch.getDescription());
            }
        }

        System.out.println("4. 수정");
        channel1 = channelService.updateChannelname(channel1.getId(), "hihihi");
        System.out.println("수정 완료! " + channel1);


        System.out.println("5. 삭제");
        if (channelService.deleteChannel(channel1.getId())) {
            System.out.println("삭제 완료");
            System.out.println("리스트: " + channelList);
        } else {
            System.out.println("삭제 실패");
        }
    }

    public static void testUser() {
        System.out.println("--------------User--------------");
        JCFUserService userService = new JCFUserService();

        System.out.println("1. 등록");
        User user1 = userService.createUser("seo", "1234", 20, "aaa@aaa");
        User user2 = userService.createUser("yeon", "5678", 25, "bbb@bbb");

        System.out.println("등록 완료! " + user1);
        System.out.println("등록 완료! " + user2);

        System.out.println("2. 보기");
        User user = userService.readUser(user1.getId());
        if (user != null) {
            System.out.println(user.getId() + " " + user.getUsername() + " " + user.getPassword() + " " + user.getEmail() + " " + user.getAge());
        } else {
            System.out.println("존재하지 않는 회원입니다.");
        }

        System.out.println("3. 모두 보기");
        List<User> userList = userService.readAllUsers();
        if (userList.isEmpty()) {
            System.out.println("존재하지 않습니다.");
        } else {
            for (User u : userList) {
                System.out.println(u.getId() + " " + u.getUsername() + " " + u.getPassword() + " " + u.getEmail() + " " + u.getAge());
            }
        }

        System.out.println("4. 수정");
        user1 = userService.updateUsername(user1.getId(), "seoseo");
        System.out.println("수정 완료! " + user1);


        System.out.println("5. 삭제");
        if (userService.deleteUser(user1.getId())) {
            System.out.println("삭제 완료");
            System.out.println("리스트: " + userList);
        } else {
            System.out.println("삭제 실패");
        }
    }

    public static void testMessage() {
        System.out.println("--------------Message--------------");
        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService();

        User user1 = userService.createUser("seo", "1234", 20, "aaa@aaa");
        User user2 = userService.createUser("yeon", "5678", 25, "bbb@bbb");
        Channel channel1 = channelService.createChannel("Hi", "안녕하세요");
        Channel channel2 = channelService.createChannel("Hello", "안녕");

        JCFMessageService messageService = new JCFMessageService(userService, channelService);

        Message message1 = messageService.createMessage(user1.getId(), channel1.getId(), "seo", "Hi","안녕하세요");
        Message message2 = messageService.createMessage(user2.getId(), channel2.getId(), "yeon", "Hello", "안녕");

        System.out.println("등록 완료! " + message1);
        System.out.println("등록 완료! " + message2);

        System.out.println("2. 보기");
        Message message = messageService.readMessage(message1.getId());
        if (message != null) {
            System.out.println(message.getId() + " " + message.getName() + " " + message.getTitle() + " " + message.getContent());
        } else {
            System.out.println("존재하지 않는 회원입니다.");
        }

        System.out.println("3. 모두 보기");
        List<Message> messageList = messageService.readAllMessages();
        if (messageList.isEmpty()) {
            System.out.println("존재하지 않습니다.");
        } else {
            for (Message m : messageList) {
                System.out.println(m.getId() + " " + m.getName() + " " + m.getTitle() + " " + m.getContent());
            }
        }

        System.out.println("4. 수정");
        message1 = messageService.updateName(message1.getId(), "seoseo");
        System.out.println("수정 완료! " + message1);


        System.out.println("5. 삭제");
        if (messageService.deleteMessage(message1.getId())) {
            System.out.println("삭제 완료");
            System.out.println("리스트: " + messageList);
        } else {
            System.out.println("삭제 실패");
        }
    }
}
