package com.sprint.mission.discodeit.app;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.jcf.JCFChannelService;
import com.sprint.mission.discodeit.jcf.JCFUserService;

public class JavaApplication {

    public static void main(String[] args) {
//        JCFUserService jcfUserService = new JCFUserService();
//        userCRUDTest(jcfUserService);

        JCFChannelService jcfChannelService = new JCFChannelService();
        channelCRUDTest(jcfChannelService);
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
        System.out.println("조회: " + jcfUserService.findById(user1.getId()));

        // 전체 조회
        System.out.println("전체 조회: " + jcfUserService.findAll());

        // 수정, 임꺽정(80세) -> 김철수(30세)
        jcfUserService.update(user2.getId(), "김철수", 30);
        System.out.println("수정 후 조회: " + jcfUserService.findById(user2.getId()));

        // 삭제, 임재범(50세) 삭제
        jcfUserService.delete(user3.getId());
        System.out.println("삭제 후 조회: " + jcfUserService.findById(user3.getId()));   // 존재하지않으므로 null 출력

        // 마지막 전체 조회
        System.out.println("전체 조회: " + jcfUserService.findAll());
    }

    private static void channelCRUDTest(JCFChannelService jcfChannelService) {
        // 3개의 채널 객체 생성
        Channel channel1 = new Channel("자바 공부 채널", "자바 스터디 채널입니다.");
        Channel channel2 = new Channel("스프링 공부 채널", "스프링 스터디 채널입니다.");
        Channel channel3 = new Channel("알고리즘 공부 채널", "알고리즘 스터디 채널입니다.");

        jcfChannelService.create(channel1);
        jcfChannelService.create(channel2);
        jcfChannelService.create(channel3);

        // 단건 조회
        System.out.println("조회: " + jcfChannelService.findById(channel1.getId()));

        // 전체 조회
        System.out.println("전체 조회: " + jcfChannelService.findAll());

        // 수정, 스프링 공부 채널 -> 운영체제 공부 채널
        jcfChannelService.update(channel2.getId(), "운영체제 공부 채널", "운영체제 스터디 채널입니다.", channel2.getCount());
        System.out.println("수정 후 조회: " + jcfChannelService.findById(channel2.getId()));

        // 삭제, 알고리즘 공부 채널
        jcfChannelService.delete(channel3.getId());
        System.out.println("삭제 후 조회: " + jcfChannelService.findById(channel3.getId()));   // 존재하지않으므로 null 출력

        // 마지막 전체 조회
        System.out.println("전체 조회: " + jcfChannelService.findAll());
    }
}
