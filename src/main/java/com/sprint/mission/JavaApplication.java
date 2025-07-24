package com.sprint.mission;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.core.ChannelManageService;
import com.sprint.mission.discodeit.service.core.ChatService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.List;

public class JavaApplication {
    public static void main(String[] args) {

        JCFUserService userService = new JCFUserService();
        JCFChannelService channelService = new JCFChannelService();
        JCFMessageService messageService = new JCFMessageService();

        ChannelManageService channelManageService = new ChannelManageService(channelService, userService);
        ChatService chatService = new ChatService(messageService, channelService);

        testUserService(userService);
        testChannelService(channelService);
        testMessageService(messageService);
        testService(channelManageService, chatService, userService, channelService);
    }

    public static void testUserService(UserService userService) {

        System.out.println("============= 유저 테스트 시작 =============");

        User user = new User("Test Target", true);

        userService.create(user);
        userService.create(new User("Test User1", false));
        userService.create(new User("Test User2", true));
        userService.create(new User("Test User3", false));
        userService.create(new User("Test User4", true));

        System.out.println("User 목록 : " + userService.getAll());

        User target = userService.get(user.getId());
        System.out.println("유저 ID로 찾기 : " + target);
        if (target != null) {
            userService.update(target.getId(), "Update Target User", false);
            System.out.println("Test Target 유저 변경 : " + target);
        }

        System.out.println("User 목록 : " + userService.getAll());

        System.out.println("Update Target User 유저 삭제");
        userService.delete(target.getId());
        System.out.println("채널 목록 : " + userService.getAll());

        System.out.println("============= 유저 테스트 끝 =============");

    }

    public static void testChannelService(ChannelService channelService) {

        System.out.println("============= 채널 테스트 시작 =============");


        User user = new User("Test Target", true);

        Channel testChannel = new Channel("Test Target", "Test Target", user.getId());

        channelService.create(testChannel);
        channelService.create(new Channel("Test Channel1", "Test Channel Description1", user.getId()));
        channelService.create(new Channel("Test Channel2", "Test Channel Description2", user.getId()));
        channelService.create(new Channel("Test Channel3", "Test Channel Description3", user.getId()));
        channelService.create(new Channel("Test Channel4", "Test Channel Description4", user.getId()));

        System.out.println("채널 목록 : " + channelService.getAll());

        Channel target = channelService.get(testChannel.getId());
        System.out.println("채널 ID로 찾기 : " + target);
        if (target != null) {
            channelService.update(target.getId(), "Update Test Target", "Update Test Target");
            System.out.println("Test Target 채널 변경 : " + target);
        }
        System.out.println("채널 목록 : " + channelService.getAll());

        System.out.println("채널에 유저 추가");
        testChannel.addUser(new User("Test add User", true).getId());
        System.out.println("채널 목록 : " + channelService.getAll());

        System.out.println("Test Target 채널 삭제");
        channelService.delete(target.getId());
        System.out.println("채널 목록 : " + channelService.getAll());

        System.out.println("============= 채널 테스트 끝 =============");
    }

    public static void testMessageService(MessageService messageService) {

        System.out.println("============= 메세지 테스트 시작 =============");

        User testUser1 = new User("Test User1", true);
        User testUser2 = new User("Test User2", true);

        Channel testChannel = new Channel("Test Channel", "Test Channel"
            , testUser1.getId(), List.of(testUser1.getId(), testUser2.getId()), null);

        Message message1 = new Message("Test Target Message From User1", testChannel.getId(), testUser1.getId());
        Message message2 = new Message("Test Message From User2", testChannel.getId(), testUser2.getId());

        testChannel.addMessage(message1.getId());
        testChannel.addMessage(message2.getId());

        messageService.create(message1);
        messageService.create(message2);

        System.out.println("Message 목록 : " + messageService.getAll());

        Message target = messageService.get(message1.getId());
        System.out.println("메세지 ID로 찾기 : " + target);
        if (target != null) {
            messageService.update(target.getId(), "Update Target Text~~");
            System.out.println("Test Target 메세지 변경 : " + target);
        }

        System.out.println("Message 목록 : " + messageService.getAll());

        System.out.println("Update Target Message 삭제");
        messageService.delete(target.getId());
        System.out.println("Message 목록 : " + messageService.getAll());
        System.out.println("============= 메세지 테스트 끝 =============");

    }

    public static void testService(ChannelManageService channelManageService, ChatService chatService, UserService userService, ChannelService channelService) {

        System.out.println("============= 채팅, 채널관리 테스트 시작 =============");

        // 유저 생성
        User adminUser = new User("adminUser", true);
        User testUser = new User("testUser", true);
        userService.create(adminUser);
        userService.create(testUser);

        Channel channel = new Channel("general", "메인 채팅방", adminUser.getId());
        channelService.create(channel);

        channelManageService.addUserToChannel(channel.getId(), testUser.getId());

        List<User> usersInChannel = channelManageService.listUsersInChannel(channel.getId());
        System.out.println("채널 유저 목록:");
        usersInChannel.forEach(u -> System.out.println("- " + u.getName()));

        chatService.sendMessage(channel.getId(), adminUser.getId(), "Hi, I'm adminUser");
        chatService.sendMessage(channel.getId(), testUser.getId(), "Hi, I'm testUser");

        System.out.println("\n채널 메시지 목록:");
        List<Message> messages = chatService.getMessagesInChannel(channel.getId());
        for (Message m : messages) {
            String userName = userService.get(m.getUserId()).getName();
            System.out.printf("[%d] %s: %s\n", m.getCreatedAt(), userName, m.getText());
        }

        channelManageService.removeUserFromChannel(channel.getId(), testUser.getId());
        System.out.println("\ntestUser 퇴장 후 채널 유저 목록:");
        channelManageService.listUsersInChannel(channel.getId())
            .forEach(u -> System.out.println("- " + u.getName()));


        System.out.println("============= 채팅, 채널관리 테스트 끝 =============");
    }
}