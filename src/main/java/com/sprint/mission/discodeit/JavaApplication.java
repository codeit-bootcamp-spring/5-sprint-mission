package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.jcf.*;

import java.util.List;
import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {

        JCFUserService userService = new JCFUserService();
        JCFChannelService channelService = new JCFChannelService();
        JCFMessageService messageService = new JCFMessageService(userService, channelService);

        // 사용자 등록
        User user = userService.create(new User("홍길동"));
        System.out.println("사용자 등록 완료: " + user.getName());

        // 채널 등록
        Channel channel = channelService.create(new Channel("일반 채널"));
        System.out.println("채널 등록 완료: " + channel.getTitle());

        // 메시지 등록
        Message message = messageService.create(new Message("안녕하세요!", user.getId(), channel.getId()));
        System.out.println("메시지 등록 완료: " + message.getContent());

        // 단건 조회
        User foundUser = userService.findById(user.getId());
        System.out.println("사용자 단건 조회: " + foundUser.getName());

        Channel foundChannel = channelService.findById(channel.getId());
        System.out.println("채널 단건 조회: " + foundChannel.getTitle());

        Message foundMessage = messageService.findById(message.getId());
        System.out.println("메시지 단건 조회: " + foundMessage.getContent());

        // 다건 조회
        System.out.println("전체 사용자 목록:");
        for (User u : userService.findAll()) {
            System.out.println("- " + u.getName());
        }

        System.out.println("전체 채널 목록:");
        for (Channel c : channelService.findAll()) {
            System.out.println("- " + c.getTitle());
        }

        System.out.println("전체 메시지 목록:");
        for (Message m : messageService.findAll()) {
            System.out.println("- " + m.getContent());
        }

        // 사용자 이름 수정
        userService.update(user.getId(), "이몽룡");
        System.out.println("사용자 이름 수정 완료");

        // 수정된 사용자 재조회
        User updatedUser = userService.findById(user.getId());
        System.out.println("수정 후 사용자 이름 확인: " + updatedUser.getName());

        // 메시지 내용 수정
        messageService.update(message.getId(), "메시지 내용 수정 완료");
        System.out.println("메시지 수정 완료");

        // 수정된 메시지 재조회
        Message updatedMessage = messageService.findById(message.getId());
        System.out.println("수정 후 메시지 내용 확인: " + updatedMessage.getContent());

        // 사용자 삭제
        userService.delete(user.getId());
        System.out.println("사용자 삭제 완료");

        // 삭제된 사용자 재조회
        User deletedUser = userService.findById(user.getId());
        if (deletedUser == null) {
            System.out.println("삭제된 사용자 확인됨 (null 반환)");
        } else {
            System.out.println("사용자 삭제 실패");
        }
    }
}
