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
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JavaApplication {

    public static void main(String[] args) {


        //createTest();
        //JCFTest();

        FILETest();

    }


    private static  void FILETest(){
        // [1] File 기반 Repository 생성
        UserRepository userRepo = new FileUserRepository();
        ChannelRepository channelRepo = new FileChannelRepository();
        MessageRepository messageRepo = new FileMessageRepository();

        // [2] Basic 서비스 생성
        UserService userService = new BasicUserService(userRepo);
        ChannelService channelService = new BasicChannelService(channelRepo);

        // [3] 유저/채널 로드
        User user = setupUser(userService);
        Channel channel = setupChannel(channelService);

        // [4] Repository 기반 데이터 전달
        Map<UUID, User> userData = userService.allFind().stream()
                .collect(HashMap::new, (m, u) -> m.put(u.getId(), u), Map::putAll);

        Map<UUID, Channel> channelData = channelService.allFind().stream()
                .collect(HashMap::new, (m, c) -> m.put(c.getId(), c), Map::putAll);

        MessageService messageService = new BasicMessageService(messageRepo, userData, channelData);
        messageCreateTest(messageService, channel, user);
    }

    private static void JCFTest(){


        UserRepository userRepo = new JCFUserRepository();
        ChannelRepository channelRepo = new JCFChannelRepository();
        MessageRepository messageRepo = new JCFMessageRepository();


        UserService userService = new BasicUserService(userRepo);
        ChannelService channelService = new BasicChannelService(channelRepo);

        User user = setupUser(userService);
        Channel channel = setupChannel(channelService);

        Map<UUID, User> userData = new HashMap<>();
        userData.put(user.getId(), user);

        Map<UUID, Channel> channelData = new HashMap<>();
        channelData.put(channel.getId(), channel);

        MessageService messageService = new BasicMessageService(messageRepo, userData, channelData);
        messageCreateTest(messageService, channel, user);

    }


    private static void createTest() {
        UserRepository userRepository = new FileUserRepository();
        ChannelRepository channelRepository = new FileChannelRepository();
        MessageRepository messageRepository = new FileMessageRepository();


        UserService userService = new BasicUserService(userRepository);
        ChannelService channelService = new BasicChannelService(channelRepository);


        Map<UUID, User> userData = new HashMap<>();
        for (User u : userService.allFind()) {
            userData.put(u.getId(), u);
        }

        Map<UUID, Channel> channelData = new HashMap<>();
        for (Channel c : channelService.allFind()) {
            channelData.put(c.getId(), c);
        }

        MessageService messageService = new BasicMessageService(messageRepository , userData, channelData);

        User user = setupUser(userService);
        Channel channel = setupChannel(channelService);

        messageCreateTest(messageService, channel, user);
    }


    static User setupUser(UserService userService) {
        User user = new User("홍길동", "1234567");
        userService.create(user);
        System.out.println("유저 생성  " + user.getUserName());
        return user;
    }

    static Channel setupChannel(ChannelService channelService) {
        Channel channel = new Channel("0000111", "수업중디코");
        channelService.create(channel);
        System.out.println("채널 생성 : " + channel.getChannelName());
        return channel;
    }


    static void messageCreateTest(MessageService messageService, Channel channel, User user) {
        Message message = new Message("새롭게 시작", "홍길동", "삼식이");
        messageService.create(message);
        System.out.println("메세지 생성 : " + message.getMessage());

    }

}
