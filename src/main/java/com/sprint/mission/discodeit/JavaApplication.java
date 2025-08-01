package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.enums.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;

import java.util.List;

import static com.sprint.mission.discodeit.entity.enums.ChannelType.*;

public class JavaApplication {

    static void userCRUDTest(UserService userService) {
        // 생성
        User user = userService.create("woody", "woody@codeit.com", "woody1234");
        System.out.println("유저 생성: " + user.getId());
        // 조회
        User foundUser = userService.findById(user.getId());
        System.out.println("유저 조회(단건): " + foundUser.getId());
        List<User> foundUsers = userService.findAll();
        System.out.println("유저 조회(다건): " + foundUsers.size());
        // 수정
        User updatedUser = userService.update(user.getId(), null, null, "woody5678");
        System.out.println("유저 수정: " + String.join("/", updatedUser.getName(), updatedUser.getEmail(), updatedUser.getPassword()));
        // 삭제
        userService.delete(user.getId());
        List<User> foundUsersAfterDelete = userService.findAll();
        System.out.println("유저 삭제: " + foundUsersAfterDelete.size());
    }

    static void channelCRUDTest(ChannelService channelService) {
        // 생성
        Channel channel = channelService.create("공지 채널입니다.", "공지", PUBLIC);
        System.out.println("채널 생성: " + channel.getId());
        // 조회
        Channel foundChannel = channelService.findById(channel.getId());
        System.out.println("채널 조회(단건): " + foundChannel.getId());
        List<Channel> foundChannels = channelService.findAll();
        System.out.println("채널 조회(다건): " + foundChannels.size());
        // 수정
        Channel updatedChannel = channelService.update(channel.getId(), "공지사항", null, PUBLIC);
        System.out.println("채널 수정: " + String.join("/", updatedChannel.getName(), updatedChannel.getDescription()));
        // 삭제
        channelService.delete(channel.getId());
        List<Channel> foundChannelsAfterDelete = channelService.findAll();
        System.out.println("채널 삭제: " + foundChannelsAfterDelete.size());
    }

    static void messageCRUDTest(UserService userService, ChannelService channelService, MessageService messageService) {
        // 셋업
        User user = userService.create("woody", "woody@codeit.com", "woody1234");
        Channel channel = channelService.create("공지 채널입니다.", "공지", ChannelType.PUBLIC);
        // 생성
        Message message = messageService.create(user.getId(), channel.getId(), "안녕하세요.");
        System.out.println("메시지 생성: " + message.getId());
        // 조회
        Message foundMessage = messageService.findById(message.getId());
        System.out.println("메시지 조회(단건): " + foundMessage.getId());
        List<Message> foundMessages = messageService.findAll();
        System.out.println("메시지 조회(다건): " + foundMessages.size());
        // 수정
        Message updatedMessage = messageService.update(message.getId(), "반갑습니다.");
        System.out.println("메시지 수정: " + updatedMessage.getContent());
        // 삭제
        messageService.delete(message.getId());
        List<Message> foundMessagesAfterDelete = messageService.findAll();
        System.out.println("메시지 삭제: " + foundMessagesAfterDelete.size());
    }


    public static void main(String[] args) {
        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService();
        MessageService messageService = new JCFMessageService(channelService, userService);

        userCRUDTest(userService);
        channelCRUDTest(channelService);
        messageCRUDTest(userService, channelService, messageService);
    }
}
