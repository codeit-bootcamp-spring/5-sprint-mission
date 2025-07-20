package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.jcf.*;

import java.util.UUID;
import java.util.NoSuchElementException;

public class Run { // 이유는 모르겠지만 자꾸 한글이 꺠집니다 ㅠㅠ, 구글링에 나온 설정은 싹다해도 결국엔 꺠져서 영어로했습니다.
                   // 그 외 Run 테스트를 어떻게해야 할지 몰라서 gpt를 썻습니다, 그 외 작업물에도 gpt 도움을 많이 받았습니다.
    public static void main(String[] args) {
        JCFUserService userService = JCFUserService.getInstance();

        // 유저 생성 시작
        System.out.println("==================================================================");
        User user = userService.create("user1", "1234");
        System.out.println("▶ User created: " + user);

        // 유저 업데이트 이름 및 패스워드 변경
        System.out.println("==================================================================");
        userService.update(user.getId(), "user1_updated", "5678");
        System.out.println("▶ User updated: " + userService.find(user.getId()));

        // 채널 인스턴스 공간 생성??
        JCFChannelService channelService = JCFChannelService.getInstance();

        // 채널 생성
        System.out.println("==================================================================");
        Channel channel = channelService.create("General");
        System.out.println("▶ Channel created: " + channel.getChName());

        // 채널 업데이트 이름변경
        System.out.println("==================================================================");
        channelService.update(channel.getChId(), "Announcement");
        System.out.println("▶ Channel name after update: " + channelService.find(channel.getChId()).getChName());

        // 메세지 인스턴스 공간 생성
        JCFMessageService messageService = JCFMessageService.getInstance();

        // 메세지 생성
        System.out.println("==================================================================");
        Message message = messageService.create("Hello!", channel.getChId(), user.getId());
        System.out.println("▶ Message created: " + message.getContent());

        // 메세지 업데이트
        System.out.println("==================================================================");
        messageService.update(message.getId(), "Hello, this is user1!");
        System.out.println("▶ Message updated: " + messageService.find(message.getId()).getContent());

        // 모든 메세지 출력
        System.out.println("==================================================================");
        System.out.println("▶ All messages:");
        messageService.findAll().forEach(m -> {
            System.out.println("[" + m.getChannelId() + "] " + m.getAuthorId() + ": " + m.getContent());
        });

        // 유저, 메세지 ,채널 삭제
        System.out.println("==================================================================");
        // Delete test (optional)
        userService.delete(user.getId());
        channelService.delete(channel.getChId());
        messageService.delete(message.getId());

        System.out.println("All entities deleted successfully");
        System.out.println("==================================================================");

        // Error test examples
        System.out.println("==================================================================");
        try {
            userService.find(UUID.randomUUID());
        } catch (NoSuchElementException e) {
            System.err.println("[Error] " + e.getMessage());
        }

        System.out.println("==================================================================");
        try {
            userService.create("", "");
        } catch (IllegalArgumentException e) {
            System.err.println("[Error] " + e.getMessage());
        }
        System.out.println("==================================================================");
    }
}
