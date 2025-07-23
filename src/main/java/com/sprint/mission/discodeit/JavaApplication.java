package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {
        UserService userService = new JCFUserService();
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
        System.out.println(messageService.searchAll().toString().replace("}, ", "},\n"));
    }
}
