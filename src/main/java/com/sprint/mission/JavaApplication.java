package com.sprint.mission;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCAMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.List;
import java.util.UUID;

public class JavaApplication {

    static UserService userService = new JCFUserService();
    static ChannelService channelService = new JCFChannelService();
    static MessageService messageService = new JCAMessageService();

    public static void main(String[] args) {

        userService.create("규섭", "1234");
        userService.create("재민", "1234");
        userService.create("지은", "1234");
        userService.create("종현", "1234");

        channelService.create("언어 공부", "Java");
        channelService.create("CS 공부", "자료구조");
        channelService.create("CS 공부", "알고리즘");
        channelService.create("CS 공부", "네트워크");

        messageService.create(userService.findAll().get(0), channelService.findAll().get(0), "안녕하세요!" );
        messageService.create(userService.findAll().get(1), channelService.findAll().get(0), "좋은 아침입니다!" );
        messageService.create(userService.findAll().get(0), channelService.findAll().get(0), "과제는 많이 하셨나요?" );
        messageService.create(userService.findAll().get(2), channelService.findAll().get(0), "안녕하세요!" );
        messageService.create(userService.findAll().get(3), channelService.findAll().get(0), "안녕하세요!" );


        testUserService();
        testChannelService();
        testMessageService();
    }

    public static void testUserService() {
        System.out.println("======== [유저 테스트] ========");

        System.out.println(userService.findAll());

        // 등록
        User user1 = userService.create("소연", "1234");
        System.out.println("등록: " + user1.getName());

        // 조회
        User found = userService.findById(user1.getId());
        System.out.println("조회: " + found.getName());
        System.out.println(userService.findAll());

        // 수정
        userService.update(user1.getId(), "이소연");
        System.out.println("수정 후: " + userService.findById(user1.getId()).getName());

        // 삭제
        userService.delete(user1.getId());

        // 삭제 확인
        User deleted = userService.findById(user1.getId());
        System.out.println("삭제 확인: " + (deleted == null ? "성공" : "실패"));
        System.out.println(userService.findAll());
    }

    public static void testChannelService() {
        System.out.println("================================== [채널 테스트] ==================================");

        System.out.println("모든 채널: ");
        System.out.println(channelService.findAll());
        System.out.println();

        Channel ch =  channelService.create("Java 공부", "언어");
        System.out.println("채널 생성: " + ch.getName() + " | 채널 주제: " +  ch.getTopic());
        System.out.println(channelService.findAll());
        System.out.println();

        String channelName = "CS";
        System.out.println(channelName + "로 검색된 채널 :");
        List<Channel> channelList = channelService.findByName(channelName);
        System.out.println(channelList);
        System.out.println();


        // 채널 이름 변경
        UUID channelId = channelList.get(0).getId();
        System.out.println("변경 전 이름: " + channelList.get(0).getName());
        Channel newChannel = channelService.updateName(channelId, "CS 공부하기!");
        System.out.println("변경된 채널명: " + newChannel.getName());
        System.out.println(channelService.findAll());
        System.out.println();

        // 채널 주제 변경
        System.out.println("변경 전 이름: " + channelList.get(0).getTopic());
        channelService.updateTopic(channelList.get(0).getId(), "컴퓨터구조");
        System.out.println("변경된 채널주제: " + newChannel.getTopic());
        System.out.println(channelService.findAll());
        System.out.println();

        // 채널 삭제
        System.out.println("삭제할 채널: " +  channelList.get(0).getName() + channelList.get(0).getTopic());
        channelService.deleteById(channelList.get(0).getId());
        System.out.println(channelService.findAll());
        System.out.println();

    }

    public static void testMessageService() {
        System.out.println("======== [메시지 테스트] ========");
        System.out.println(messageService.findAll());

        // 메시지 생성
        Message m1 = messageService.create(userService.findAll().get(3), channelService.findAll().get(0), "아니요.. 다 못했어요" );
        System.out.println(m1);

        // 메시지 검색
        System.out.println("검색된 메시지: ");
        List<Message> findedMsg = messageService.findByStr("안녕");
        System.out.println(findedMsg);
        System.out.println();

        //메시지 수정
        Message m2 = messageService.update(findedMsg.get(2).getId(), "안녕하심까!");
        System.out.println("메시지가 수정되었습니다");
        System.out.println(messageService.findAll());
        System.out.println();

        //메시지 삭제
        messageService.deleteById(messageService.findAll().get(5).getId());
        System.out.println(messageService.findAll());
    }
}