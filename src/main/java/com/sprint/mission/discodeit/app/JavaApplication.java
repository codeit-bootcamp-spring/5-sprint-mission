package com.sprint.mission.discodeit.app;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.jcf.JCFChannelService;
import com.sprint.mission.discodeit.jcf.JCFUserService;
import com.sprint.mission.discodeit.jcf.JFCMessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JavaApplication {

    public static void main(String[] args) {
        JCFUserService jcfUserService = new JCFUserService();
        List<User> users = userCRUDTest(jcfUserService);

        JCFChannelService jcfChannelService = new JCFChannelService();
        channelCRUDTest(jcfChannelService, jcfUserService, users);

        JFCMessageService jfcMessageService = new JFCMessageService();
        messageCRUDTest(jfcMessageService);
    }

    private static List<User> userCRUDTest(JCFUserService jcfUserService) {
        // 3개의 유저 객체 생성
        User user1 = new User("임재혁", 27);
        User user2 = new User("임꺽정", 80);
        User user3 = new User("임재범", 50);

        jcfUserService.create(user1);
        jcfUserService.create(user2);
        jcfUserService.create(user3);

        // 단건 조회
        System.out.println("유저 조회: " + jcfUserService.findById(user1.getId()));

        // 전체 조회
        System.out.println("전체 채널 조회: " + jcfUserService.findAll());

        // 수정, 임꺽정(80세) -> 김철수(30세)
        jcfUserService.update(user2.getId(), "김철수", 30);
        System.out.println("유저 정보 수정 후 조회: " + jcfUserService.findById(user2.getId()));

        // 삭제, 임재범(50세) 삭제
        jcfUserService.delete(user3.getId());
        System.out.println("유저 삭제 후 조회: " + jcfUserService.findById(user3.getId()));   // 존재하지않으므로 null 출력

        // 마지막 전체 조회
        System.out.println("전체 유저 조회: " + jcfUserService.findAll());

        // 이후 테스트들에서 재사용하기 위해 유저 객체 리스트 반환
        return List.of(user1, user2);
    }

    private static void channelCRUDTest(JCFChannelService jcfChannelService, JCFUserService jcfUserService, List<User> users) {
        // 기존 객체 재사용
        User user1 = users.get(0);
        User user2 = users.get(1);

        List<UUID> userIds1 = new ArrayList<>(List.of(user1.getId(), user2.getId()));
        List<UUID> userIds2 = new ArrayList<>(List.of(user2.getId()));

        // 3개의 채널 객체 생성
        Channel channel1 = new Channel(userIds1, "자바 공부 채널", "자바 스터디 채널입니다.");
        Channel channel2 = new Channel(userIds2, "스프링 공부 채널", "스프링 스터디 채널입니다.");

        jcfChannelService.create(channel1);
        jcfChannelService.create(channel2);

        // 유저 객체에 참여 정보 추가
        jcfUserService.joinChannel(user1.getId(), channel1.getId());
        jcfUserService.joinChannel(user2.getId(), channel1.getId());
        jcfUserService.joinChannel(user2.getId(), channel2.getId());

        // 채널 & 유저 전체 조회 (서로 채널아이디, 유저아이디 잘 추가되었는지 확인)
        System.out.println("전체 채널 조회: " + jcfChannelService.findAll());
        System.out.println("전체 유저 조회: " + jcfUserService.findAll());

        // 수정, 스프링 공부 채널 -> 운영체제 공부 채널
        jcfChannelService.update(channel2.getId(), "운영체제 공부 채널", "운영체제 스터디 채널입니다.");
        System.out.println("채널 정보 수정 후 조회: " + jcfChannelService.findById(channel2.getId()));

        // 삭제, 알고리즘 공부 채널 +  유저2(김철수) 채널 참여 정보에서 알고리즘 공부 채널 제거
        jcfChannelService.delete(channel2.getId());
        jcfUserService.leaveChannel(user2.getId(), channel2.getId());
        System.out.println("채널 삭제 후 조회: " + jcfChannelService.findById(channel2.getId()));   // 존재하지않으므로 null 출력

        // 마지막 전체 조회
        System.out.println("전체 채널 조회: " + jcfChannelService.findAll());
        System.out.println("전체 유저 조회: " + jcfUserService.findAll());
    }

    private static void messageCRUDTest(JFCMessageService jfcMessageService) {
        // 메세지 보낼 객체 생성
        User user1 = new User("임재혁", 27);
        User user2 = new User("임꺽정", 80);
        User user3 = new User("임재범", 50);

        List<UUID> userIds1 = List.of(user1.getId(), user2.getId());
        List<UUID> userIds2 = List.of(user2.getId(), user3.getId());

        // 메세지를 주고 받을 채널 객체 생성
        Channel channel1 = new Channel(userIds1, "자바 공부 채널", "자바 스터디 채널입니다.");
        Channel channel2 = new Channel(userIds2, "스프링 공부 채널", "스프링 스터디 채널입니다.");

        // 메세지 객체 생성
        Message message1 = new Message(user1.getId(), user2.getId(), "안녕하세요 여러분 자바 즐거우세요?");
        Message message2 = new Message(user2.getId(), user3.getId(), "안녕하세요 여러분 스프링 즐거우세요?");

        jfcMessageService.create(message1);
        jfcMessageService.create(message2);

        // 단건 조회
        System.out.println("조회: " + jfcMessageService.findById(message1.getId()));

        // 전체 조회
        System.out.println("전체 조회: " + jfcMessageService.findAll());

        // 수정, "안녕하세요 여러분 자바 즐거우세요?" -> "안녕하세요 여러분 다들 OOP 개념에 대해서 익숙해지셨나요?"
        jfcMessageService.update(message1.getId(), "안녕하세요 여러분 다들 OOP 개념에 대해서 익숙해지셨나요?");
        System.out.println("수정 후 조회: " + jfcMessageService.findById(message1.getId()));

        // 삭제, "안녕하세요 여러분 스프링 즐거우세요?"
        jfcMessageService.delete(message2.getId());
        System.out.println("삭제 후 조회: " + jfcMessageService.findById(message2.getId()));   // 존재하지않으므로 null 출력

        // 마지막 전체 조회
        System.out.println("전체 조회: " + jfcMessageService.findAll());
    }
}
