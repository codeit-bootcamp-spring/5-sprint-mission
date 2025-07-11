package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.jcf.*;

import java.util.List;
import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {
        // 서비스 초기화
        JCFUserService userService = new JCFUserService();
        JCFChannelService channelService = new JCFChannelService();
        JCFMessageService messageService = new JCFMessageService();

        // 사용자 생성
        User user1 = new User("김유민");
        userService.create(user1);
        User user2 = new User("코드잇");
        userService.create(user2);

        // 사용자 단건 조회
        System.out.println(" 사용자 단건 조회 결과:");
        System.out.println(userService.findById(user1.getId()));

        // 사용자 전체 조회
        System.out.println("\n 전체 사용자 목록:");
        for (User user : userService.findAll()) {
            System.out.println(user);
        }

        // 사용자 이름 수정
        user1.updateName("김유민(수정됨)");
        userService.update(user1.getId(), user1);
        System.out.println("\n 사용자 이름 수정 후 조회:");
        System.out.println(userService.findById(user1.getId()));

        // 채널 생성
        Channel channel1 = new Channel("일반");
        channelService.create(channel1);
        Channel channel2 = new Channel("자유");
        channelService.create(channel2);

        // 채널 전체 조회
        System.out.println("\n 전체 채널 목록:");
        for (Channel channel : channelService.findAll()) {
            System.out.println(channel);
        }

        // 메시지 생성
        Message msg1 = new Message("안녕하세요 여러분!", user1.getId(), channel1.getId());
        messageService.create(msg1);
        Message msg2 = new Message("반갑습니다!", user2.getId(), channel1.getId());
        messageService.create(msg2);

        // 메시지 전체 조회
        System.out.println("\n 전체 메시지 목록:");
        for (Message msg : messageService.findAll()) {
            System.out.println(msg);
        }

        // 메시지 수정
        msg1.updateContent("(수정됨)");
        messageService.update(msg1.getId(), msg1);
        System.out.println("\n 메시지 수정 후 결과:");
        System.out.println(messageService.findById(msg1.getId()));

        // 사용자 삭제
        userService.delete(user2.getId());
        System.out.println("\n 사용자 삭제 후 전체 목록:");
        for (User user : userService.findAll()) {
            System.out.println(user);
        }
    }
}

