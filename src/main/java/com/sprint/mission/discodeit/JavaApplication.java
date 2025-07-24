package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.nio.file.Path;
import java.util.UUID;

public class JavaApplication {

    static UserService userService = new FileUserService(Path.of("/Users/apple/dev_source/5-sprint-mission/userDirectory/"));
    static ChannelService channelService = new FileChannelService(Path.of("/Users/apple/dev_source/5-sprint-mission/channelDirectory/"));
    static MessageService messageService = new FileMessageService(Path.of("/Users/apple/dev_source/5-sprint-mission/messageDirectory/"));

    public static void main(String[] args) {
        testUserService();
        testChannelService();
        testMessageService();
        /*UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService();
        MessageService messageService = new JCFMessageService();

        // 생성
        userService.create(new User("testUser1"));
        userService.create(new User("testUser2"));
        userService.create(new User("testuser3"));
        channelService.create(new Channel("testChannel1"));
        channelService.create(new Channel("testChannel2"));
        channelService.create(new Channel("testchannel3"));
        messageService.create(new Message("test Message1", userService.searchByIndex(0).getId()));
        messageService.create(new Message("test Message2", userService.searchByIndex(0).getId()));
        messageService.create(new Message("test message3", userService.searchByIndex(1).getId()));

        // 조회
        System.out.println(userService.searchByIndex(0));
        System.out.println(channelService.searchByIndex(0));
        System.out.println(messageService.searchByIndex(0));
        System.out.println("조회 테스트 시작\n" + userService.searchByName("User").toString().replace("}, ", "},\n"));
        System.out.println(channelService.searchByName("Channel").toString().replace("}, ", "},\n"));
        System.out.println(messageService.searchByContent("Mess").toString().replace("}, ", "},\n"));
        System.out.println(messageService.searchBySenderId(userService.searchByIndex(0).getId()).toString().replace("}, ", "},\n"));
        System.out.println("테스트 끝");
        System.out.println(userService.searchById(userService.searchByIndex(0).getId()));
        System.out.println(channelService.searchById(channelService.searchByIndex(0).getId()));
        System.out.println(messageService.searchById(messageService.searchByIndex(0).getId()));
        System.out.println("---------------------------------------------------------------------------------");

        // 모두 조회
        System.out.println(userService.searchAll().toString().replace("}, ", "},\n"));
        System.out.println(channelService.searchAll().toString().replace("}, ", "},\n"));
        System.out.println(messageService.searchAll().toString().replace("}, ", "},\n"));
        System.out.println("---------------------------------------------------------------------------------");

        // 수정 및 조회
        userService.searchByIndex(0).addChannel(channelService.searchByIndex(0));
        userService.searchByIndex(0).addChannel(channelService.searchByIndex(1));
        channelService.searchByIndex(0).addUser(userService.searchByIndex(0));
        channelService.searchByIndex(0).addUser(userService.searchByIndex(1));
        userService.update(userService.searchByIndex(0).updateName("updatedName"));
        channelService.update(channelService.searchByIndex(0).updateName("updatedName"));
        messageService.update(messageService.searchByIndex(0).updateContent("updatedContent"));
        System.out.println(userService.searchByIndex(0));
        System.out.println(channelService.searchByIndex(0));
        System.out.println(messageService.searchByIndex(0));
        System.out.println("Channels of User");
        for (UUID channelId : userService.searchByIndex(0).getChannels()) {
            System.out.println(channelService.searchById(channelId).toString());
        }
        System.out.println("Users of Channel");
        for (UUID userId : channelService.searchByIndex(0).getUsers()) {
            System.out.println(userService.searchById(userId).toString());
        }
        userService.searchByIndex(0).deleteChannel(channelService.searchByIndex(0));
        channelService.searchByIndex(0).deleteUser(userService.searchByIndex(0));
        System.out.println("Channels of User");
        for (UUID channelId : userService.searchByIndex(0).getChannels()) {
            System.out.println(channelService.searchById(channelId).toString());
        }
        System.out.println("Users of Channel");
        for (UUID userId : channelService.searchByIndex(0).getUsers()) {
            System.out.println(userService.searchById(userId).toString());
        }

        System.out.println("---------------------------------------------------------------------------------");

        // 삭제 및 조회
        userService.delete(userService.searchByIndex(1));
        channelService.delete(channelService.searchByIndex(0));
        messageService.delete(messageService.searchByIndex(0));

        System.out.println(userService.searchAll().toString().replace("}, ", "},\n"));
        System.out.println(channelService.searchAll().toString().replace("}, ", "},\n"));
        System.out.println(messageService.searchAll().toString().replace("}, ", "},\n"));*/
    }

    static void testUserService() {
        System.out.println("유저 서비스 테스트 --------------------------------------------");
        userService.searchAll().forEach(System.out::println);

        // 등록
        User user1 = new User("test1");
        userService.create(user1);

        // 조회
        System.out.println(userService.searchById(user1.getId()));

        // 수정
        System.out.println("수정 전 : " + userService.searchById(user1.getId()));
        userService.update(user1.updateName("updatedName"));
        System.out.println("수정 후 : " + userService.searchById(user1.getId()));

        // 삭제
        userService.delete(user1);

        // 조회
        System.out.println(userService.searchById(user1.getId()));
        userService.searchAll().forEach(System.out::println);

    }

    static void testChannelService() {
        System.out.println("채널 서비스 테스트 --------------------------------------------");
        channelService.searchAll().forEach(System.out::println);

        // 등록
        Channel channel1 = new Channel("test1");
        channelService.create(channel1);

        // 조회
        System.out.println(channelService.searchById(channel1.getId()));

        // 수정
        System.out.println("수정 전 : " + channelService.searchById(channel1.getId()));
        channelService.update(channel1.updateName("updatedName"));
        System.out.println("수정 후 : " + channelService.searchById(channel1.getId()));

        // 삭제
        channelService.delete(channel1);

        // 조회
        System.out.println(channelService.searchById(channel1.getId()));
        channelService.searchAll().forEach(System.out::println);

    }

    static void testMessageService() {
        System.out.println("메세지 서비스 테스트 --------------------------------------------");
        messageService.searchAll().forEach(System.out::println);

        // 등록
        Message message1 = new Message("test message1", userService.searchAll().get(0).getId());
        messageService.create(message1);

        // 조회
        System.out.println(messageService.searchById(message1.getId()));

        // 수정
        System.out.println("수정 전 : " + messageService.searchById(message1.getId()));
        messageService.update(message1.updateContent("updated content"));
        System.out.println("수정 후 : " + messageService.searchById(message1.getId()));

        // 삭제
        messageService.delete(message1);

        // 조회
        System.out.println(messageService.searchById(message1.getId()));
        messageService.searchAll().forEach(System.out::println);

    }

}
