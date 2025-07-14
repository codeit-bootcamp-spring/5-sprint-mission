package com.sprint.mission.discodeit.app;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.jcf.JCFChannelService;
import com.sprint.mission.discodeit.jcf.JCFUserService;
import com.sprint.mission.discodeit.jcf.JFCMessageService;
import com.sprint.mission.discodeit.jcf.SimpleChatService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JavaApplication {

    public static void main(String[] args) {
        JCFUserService jcfUserService = new JCFUserService();
        JCFChannelService jcfChannelService = new JCFChannelService();
        JFCMessageService jfcMessageService = new JFCMessageService();
        SimpleChatService simpleChatService = new SimpleChatService(jcfUserService, jcfChannelService, jfcMessageService);

        userCRUDTest(jcfUserService);
        channelCRUDTest(jcfChannelService, jcfUserService, simpleChatService);
        messageCRUDTest(jfcMessageService, jcfChannelService, jcfUserService, simpleChatService);
    }

    private static void userCRUDTest(JCFUserService jcfUserService) {
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
    }

    private static void channelCRUDTest(JCFChannelService jcfChannelService, JCFUserService jcfUserService, SimpleChatService simpleChatService) {
        System.out.println();

        // 기존 객체 재사용
        List<User> users = jcfUserService.findAll();
        User user1 = users.get(0);
        User user2 = users.get(1);

        List<UUID> userIds1 = new ArrayList<>(List.of(user1.getId(), user2.getId()));
        List<UUID> userIds2 = new ArrayList<>(List.of(user2.getId()));

        // 3개의 채널 객체 생성
        Channel channel1 = new Channel(userIds1, "자바 공부 채널", "자바 스터디 채널입니다.");
        Channel channel2 = new Channel(userIds2, "스프링 공부 채널", "스프링 스터디 채널입니다.");

        jcfChannelService.create(channel1);
        jcfChannelService.create(channel2);

        // 채널 참여 (유저&채널)
        simpleChatService.joinChannel(user1.getId(), channel1.getId());
        simpleChatService.joinChannel(user2.getId(), channel1.getId());
        simpleChatService.joinChannel(user2.getId(), channel2.getId());

        // 채널 & 유저 전체 조회 (서로 채널아이디, 유저아이디 잘 추가되었는지 확인)
        System.out.println("전체 채널 조회: " + jcfChannelService.findAll());
        System.out.println("전체 유저 조회: " + jcfUserService.findAll());

        // 수정, 스프링 공부 채널 -> 운영체제 공부 채널
        jcfChannelService.update(channel2.getId(), "운영체제 공부 채널", "운영체제 스터디 채널입니다.");
        System.out.println("채널 정보 수정 후 조회: " + jcfChannelService.findById(channel2.getId()));

        // 삭제, 알고리즘 공부 채널 +  유저2 채널 참여 정보에서 알고리즘 공부 채널 제거
        jcfChannelService.delete(channel2.getId());
        jcfUserService.leaveChannel(user2.getId(), channel2.getId());
        System.out.println("채널 삭제 후 조회: " + jcfChannelService.findById(channel2.getId()));   // 존재하지않으므로 null 출력

        // 유저1 채널2에 참가
        simpleChatService.joinChannel(user1.getId(), channel2.getId());

        // 유저1 채널1에서 나감
        simpleChatService.leaveChannel(user1.getId(), channel1.getId());

        // 마지막 전체 조회
        System.out.println("전체 채널 조회: " + jcfChannelService.findAll());
        System.out.println("전체 유저 조회: " + jcfUserService.findAll());
    }

    private static void messageCRUDTest(JFCMessageService jfcMessageService, JCFChannelService jcfChannelService, JCFUserService jcfUserService, SimpleChatService simpleChatService) {
        System.out.println();

        // 유저와 채널 조회
        List<User> users = jcfUserService.findAll();
        User user1 = users.get(0);
        User user2 = users.get(1);

        List<Channel> channels = jcfChannelService.findAll();
        Channel channel1 = channels.getFirst();

        // 유저1 채널1에 재참가
        simpleChatService.joinChannel(user1.getId(), channel1.getId());

        // 메세지 전송
        simpleChatService.sendMessage(user1.getId(), channel1.getId(), "안녕하세요 여러분 자바 즐거우세요?");
        simpleChatService.sendMessage(user2.getId(), channel1.getId(), "안녕하세요 여러분 스프링 즐거우세요?");

        // 채널 조회 (뷰 기능 활용)
        simpleChatService.viewChannel(channel1.getId());

        // 메시지 수정
        var messages = jfcMessageService.findAll();
        if (!messages.isEmpty()) {
            Message messageToUpdate = messages.getFirst();
            jfcMessageService.update(messageToUpdate.getId(), "안녕하세요 여러분 OOP 개념은 익숙하신가요?");
            System.out.println("메시지 수정 완료");
        }

        // 메시지 삭제
        if (messages.size() > 1) {
            Message messageToDelete = messages.get(1);
            jfcMessageService.delete(messageToDelete.getId());
            System.out.println("메시지 삭제 완료");
        }

        // 유저1 삭제
        jcfUserService.delete(user1.getId());

        // 채널 조회 재출력 (변화 확인)
        simpleChatService.viewChannel(channel1.getId());

        // 전체 메시지 조회
        System.out.println("전체 메시지 목록:");
        for (Message m : jfcMessageService.findAll()) {
            System.out.println("- " + m);
        }
    }
}
