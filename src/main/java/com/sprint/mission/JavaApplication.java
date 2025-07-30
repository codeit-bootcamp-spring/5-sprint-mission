package com.sprint.mission;


import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

public class JavaApplication {
    public static void main(String[] args){
        // 레포지토리 초기화
        UserRepository userRepository = new FileUserRepository();
        ChannelRepository channelRepository = new FileChannelRepository();
//        MessageRepository messageRepository = new FileMessageRepository();

        // 서비스 초기화
        UserService userService = new BasicUserService(userRepository);
        ChannelService channelService = new BasicChannelService(channelRepository, userRepository);
//        MessageService messageService = new BasicMessageService(messageRepository, channelRepository, userRepository);

        // 셋업
        User user = setupUser (userService);
        Channel channel = setupChannel(channelService);

        // 테스트
//        MessageCreateTest(messageService, channel, user);


    }

    private static User setupUser(UserService userService) {
        try {
            User user = userService.registerUser("홍길동", "test4@codeit.com", "1234");
            System.out.println("[App]User registered successfully: " + user.getName());
            return user;
        } catch (RuntimeException e) {
            System.err.println("[App]User registration failed" + e.getMessage());
            throw e;
        }
    }

    private static Channel setupChannel(ChannelService channelService) {
        try {
            Channel channel = channelService.createChannel("공지사항");
            System.out.println("[App]Channel created successfully: " + channel.getName());
            return channel;
        } catch (RuntimeException e) {
            System.err.println("[App]Channel creation failed: " + e.getMessage());
            throw e;
        }
    }

}




