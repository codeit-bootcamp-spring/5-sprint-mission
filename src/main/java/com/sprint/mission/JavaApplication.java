package com.sprint.mission;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.respository.file.*;
import com.sprint.mission.discodeit.respository.jcf.*;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.basic.*;
import com.sprint.mission.discodeit.service.file.*;
import com.sprint.mission.discodeit.service.jcf.*;

public class JavaApplication {
    static User setupUser(UserService userService) {
        return userService.create("woody", "woody1234");
    }

    static Channel setupChannel(ChannelService channelService) {
        return channelService.create("공지", "공지 채널입니다.");
    }

    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
        Message message = messageService.create(author, channel, "안녕하세요.");
        System.out.println("메시지 생성: " + message.getContent());
    }

    public static void main(String[] args) {
        System.out.println("🧪 File*Service 테스트");
        testFileService();

        System.out.println("🧪 JCF*Service 테스트");
        testJCFService();

        System.out.println("🧪 Basic*Service + JCFRepository 테스트");
        testBasicServiceWithJCFRepository();

        System.out.println("🧪 Basic*Service + FileRepository 테스트");
        testBasicServiceWithFileRepository();
    }

    public static void testFileService() {
        UserService userService = new FileUserService();
        ChannelService channelService = new FileChannelService();
        MessageService messageService = new FileMessageService();

        User user = setupUser(userService);
        Channel channel = setupChannel(channelService);
        messageCreateTest(messageService, channel, user);
    }

    public static void testJCFService() {
        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService();
        MessageService messageService = new JCFMessageService(userService, channelService);

        User user = setupUser(userService);
        Channel channel = setupChannel(channelService);
        messageCreateTest(messageService, channel, user);
    }

    // Basic + JCFRepository
    public static void testBasicServiceWithJCFRepository() {
        UserService userService = new BasicUserService(new JCFUserRepository());
        ChannelService channelService = new BasicChannelService(new JCFChannelRepository());
        MessageService messageService = new BasicMessageService(new JCFMessageRepository());

        User user = setupUser(userService);
        Channel channel = setupChannel(channelService);
        messageCreateTest(messageService, channel, user);
    }

    // Basic + FileRepository
    public static void testBasicServiceWithFileRepository() {
        UserService userService = new BasicUserService(new FileUserRepository());
        ChannelService channelService = new BasicChannelService(new FileChannelRepository());
        MessageService messageService = new BasicMessageService(new FileMessageRepository());

        User user = userService.create("woody_file", "woody1234");
        Channel channel = setupChannel(channelService);
        messageCreateTest(messageService, channel, user);
    }
}