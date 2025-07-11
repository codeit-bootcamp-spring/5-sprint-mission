package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.jcf.*;

import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {
        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService();
        MessageService messageService = new JCFMessageService();

        // ===== User 테스트 =====
        System.out.println("\n[User 테스트]");
        // 등록
        User user = new User("김유민");
        userService.create(user);
        System.out.println(" User 등록 완료: " + user);

        // 단건 조회
        System.out.println(" 단건 조회: " + userService.findById(user.getId()));

        // 전체 조회
        System.out.println(" 전체 사용자 목록: " + userService.findAll());

        // 수정
        user.updateName("김유민(수정)");
        userService.update(user.getId(), user);
        System.out.println(" 이름 수정 후 조회: " + userService.findById(user.getId()));

        // 삭제
        userService.delete(user.getId());
        System.out.println(" 삭제 후 전체 사용자 목록: " + userService.findAll());

        // ===== Channel 테스트 =====
        System.out.println("\n[Channel 테스트]");
        // 등록
        Channel channel = new Channel("자유 채팅방");
        channelService.create(channel);
        System.out.println(" Channel 등록 완료: " + channel);

        // 단건 조회
        System.out.println(" 단건 조회: " + channelService.findById(channel.getId()));

        // 전체 조회
        System.out.println(" 전체 채널 목록: " + channelService.findAll());

        // 수정
        channel.updateName("과제 채팅방");
        channelService.update(channel.getId(), channel);
        System.out.println(" 채널명 수정 후 조회: " + channelService.findById(channel.getId()));

        // 삭제
        channelService.delete(channel.getId());
        System.out.println(" 삭제 후 전체 채널 목록: " + channelService.findAll());

        // ===== Message 테스트 =====
        System.out.println("\n[Message 테스트]");
        // 등록
        UUID dummyUserId = UUID.randomUUID();
        UUID dummyChannelId = UUID.randomUUID();
        Message msg = new Message("멘토 회의는 화요일 저녁 8시에 시작합니다.", dummyUserId, dummyChannelId);
        messageService.create(msg);
        System.out.println(" Message 등록 완료: " + msg);

        // 단건 조회
        System.out.println(" 단건 조회: " + messageService.findById(msg.getId()));

        // 전체 조회
        System.out.println(" 전체 메시지 목록: " + messageService.findAll());

        // 수정
        msg.updateContent("회의는 오후 6시로 변경되었습니다.");
        messageService.update(msg.getId(), msg);
        System.out.println("✏ 메시지 수정 후 조회: " + messageService.findById(msg.getId()));

        // 삭제
        messageService.delete(msg.getId());
        System.out.println(" 삭제 후 전체 메시지 목록: " + messageService.findAll());
    }
}



