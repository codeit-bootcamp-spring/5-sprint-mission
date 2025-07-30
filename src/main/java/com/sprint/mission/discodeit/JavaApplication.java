package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.jcf.*;

public class JavaApplication {
    public static void main(String[] args) {
        var userService = new JCFUserService();
        var channelService = new JCFChannelService();
        var messageService = new JCFMessageService();

        // 등록
        User user = new User("홍길동");
        userService.create(user);

        Channel channel = new Channel("general");
        channelService.create(channel);

        Message message = new Message(user.getId(), channel.getId(), "Hello, world!");
        messageService.create(message);

        // 조회
        System.out.println("User: " + userService.read(user.getId()).getUsername());
        System.out.println("Channel: " + channelService.read(channel.getId()).getName());
        System.out.println("Message: " + messageService.read(message.getId()).getContent());

        // 수정
        userService.update(user.getId(), "홍길동");
        channelService.update(channel.getId(), "random");
        messageService.update(message.getId(), "Hi there!");

        // 수정된 데이터 확인
        System.out.println("Updated User: " + userService.read(user.getId()).getUsername());
        System.out.println("Updated Channel: " + channelService.read(channel.getId()).getName());
        System.out.println("Updated Message: " + messageService.read(message.getId()).getContent());

        // 삭제
        userService.delete(user.getId());
        channelService.delete(channel.getId());
        messageService.delete(message.getId());

        // 삭제 확인
        System.out.println("Deleted User: " + userService.read(user.getId()));
        System.out.println("Deleted Channel: " + channelService.read(channel.getId()));
        System.out.println("Deleted Message: " + messageService.read(message.getId()));
    }
}
