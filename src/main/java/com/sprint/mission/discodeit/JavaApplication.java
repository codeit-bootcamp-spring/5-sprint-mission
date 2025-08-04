package com.sprint.mission.discodeit;


import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

public class JavaApplication {
    public static void main(String[] args) {
        // 레포지토리 초기화
        UserRepository userRepository = new FileUserRepository();
        ChannelRepository channelRepository = new FileChannelRepository();
        MessageRepository messageRepository = new FileMessageRepository();

        // 서비스 초기화
        UserService userService = new BasicUserService(userRepository);
        ChannelService channelService = new BasicChannelService(channelRepository, userRepository);
        MessageService messageService = new BasicMessageService(messageRepository, channelRepository, userRepository);

        // 셋업
        User user = setupUser(userService, "홍길동", "test1@codeit.com", "1234");
        Channel channel = setupChannel(channelService, "공지사항");


        // 테스트
        channelJoinTest(channelService, user, channel);
        Message message = setupMessage(user, channel);
        messageCreateTest(messageService, message);


    }


    private static User setupUser(UserService userService, String name, String email, String password) {
        try {
            User user = userService.registerUser(name, email, password);
            System.out.println("[setup] User setup successful: " + user.getName());
            return user;
        } catch (RuntimeException e) {
            throw new RuntimeException("[setup] User setup failed: ", e);
        }
    }

    private static Channel setupChannel(ChannelService channelService, String name) {
        try {
            Channel channel = channelService.createChannel(name);
            System.out.println("[setup] Channel setup successful: " + channel.getChannelName());
            return channel;
        } catch (RuntimeException e) {
            throw new RuntimeException("[setup] Channel setup failed: ", e);
        }
    }

    private static Message setupMessage(User user, Channel channel) {
        Message message = new Message(user.getId(), channel.getId(), "안녕하세요");
        System.out.println("[setup] Message setup successful: " + message.getId());
        return message;
    }

    private static void channelJoinTest(ChannelService channelService, User user, Channel channel) {
        try {
            boolean added = channelService.addUserToChannel(channel.getId(), user.getId());
            if (added) {
                System.out.println("[Test] User '" + user.getName() + "'is Channel '" + channel.getChannelName() + "'successfully added");
            } else {
                throw new RuntimeException("[Test] User '" + user.getName() + "'is Channel '" + channel.getChannelName() + "'failed to add");
            }
        } catch (RuntimeException e) {
            System.err.println("[Test]Failed to add: " + e.getMessage());
        }
    }

    private static void messageCreateTest(MessageService messageService, Message message) {
        try {
            messageService.create(message);
            System.out.println("[Test]Message created successful: " + message.getId());
        } catch (RuntimeException e) {
            System.err.println("[Test]Message created failed: " + e.getMessage());
        }
    }
}




