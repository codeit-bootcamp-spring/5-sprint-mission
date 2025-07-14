package com.sprint.mission.discodeit.app;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.jcf.JCFChannelService;
import com.sprint.mission.discodeit.jcf.JCFUserService;
import com.sprint.mission.discodeit.jcf.JCFMessageService;
import com.sprint.mission.discodeit.jcf.SimpleChatService;

import java.util.List;

public class JavaApplication {

    public static void main(String[] args) {
        JCFUserService jcfUserService = new JCFUserService();
        JCFChannelService jcfChannelService = new JCFChannelService();
        JCFMessageService JCFMessageService = new JCFMessageService();
        SimpleChatService simpleChatService = new SimpleChatService(jcfUserService, jcfChannelService, JCFMessageService);

        userCRUDTest(jcfUserService);
        channelCRUDTest(jcfChannelService, jcfUserService, simpleChatService);
        messageCRUDTest(JCFMessageService, jcfChannelService, jcfUserService, simpleChatService);
        edgeCaseTest(simpleChatService, jcfUserService, jcfChannelService, JCFMessageService);
    }

    private static void userCRUDTest(JCFUserService jcfUserService) {
        System.out.println("\n=== 유저 테스트 시작 ===");

        // 3개의 유저 객체 생성
        User user1 = new User("임재혁", 27);
        User user2 = new User("임꺽정", 80);
        User user3 = new User("임재범", 50);

        jcfUserService.create(user1);
        jcfUserService.create(user2);
        jcfUserService.create(user3);

        // 단건 조회
        jcfUserService.findById(user3.getId(), true);

        // 전체 조회
        jcfUserService.findAll();

        // 수정, 임꺽정(80세) -> 김철수(30세)
        jcfUserService.update(user2.getId(), "김철수", 30);
        jcfUserService.findById(user3.getId(), true);

        // 삭제, 임재범(50세) 삭제
        jcfUserService.delete(user3.getId());
        jcfUserService.findById(user3.getId(), true);  // 존재하지않으므로 null 출력

        // 마지막 전체 조회
        jcfUserService.findAll();
    }

    private static void channelCRUDTest(JCFChannelService jcfChannelService, JCFUserService jcfUserService, SimpleChatService simpleChatService) {
        System.out.println("\n=== 채널 테스트 시작 ===");

        // 기존 객체 재사용
        List<User> users = jcfUserService.findAll();
        User user1 = users.get(0);
        User user2 = users.get(1);

        // 3개의 채널 객체 생성
        Channel channel1 = new Channel("자바 공부 채널", "자바 스터디 채널입니다.");
        Channel channel2 = new Channel("스프링 공부 채널", "스프링 스터디 채널입니다.");

        jcfChannelService.create(channel1);
        jcfChannelService.create(channel2);

        // 채널 참여 (유저&채널)
        simpleChatService.joinChannel(user1.getId(), channel1.getId());
        simpleChatService.joinChannel(user2.getId(), channel1.getId());
        simpleChatService.joinChannel(user2.getId(), channel2.getId());

        // 채널 & 유저 조회 (서로 채널아이디, 유저아이디 잘 추가되었는지 확인)
        jcfChannelService.findAll();
        jcfUserService.findAll();

        // 수정, 스프링 공부 채널 -> 운영체제 공부 채널
        jcfChannelService.update(channel2.getId(), "운영체제 공부 채널", "운영체제 스터디 채널입니다.");
        jcfChannelService.findById(channel2.getId(), true);

        // 삭제, 알고리즘 공부 채널 +  유저2 채널 참여 정보에서 알고리즘 공부 채널 제거
        jcfChannelService.delete(channel2.getId());
        jcfUserService.leaveChannel(user2.getId(), channel2.getId());
        jcfChannelService.findById(channel2.getId(), true);  // 존재하지않으므로 null 출력

        // 유저1 채널2에 참가
        simpleChatService.joinChannel(user1.getId(), channel2.getId());

        // 유저1 채널1에서 나감
        simpleChatService.leaveChannel(user1.getId(), channel1.getId());

        // 마지막 전체 조회
        jcfChannelService.findAll();
        jcfUserService.findAll();
    }

    private static void messageCRUDTest(JCFMessageService JCFMessageService, JCFChannelService jcfChannelService, JCFUserService jcfUserService, SimpleChatService simpleChatService) {
        System.out.println("\n=== 메세지 테스트 시작 ===");

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
        System.out.println();
        simpleChatService.viewChannel(channel1.getId());
        System.out.println();

        // 메시지 수정
        var messages = JCFMessageService.findAll();
        if (!messages.isEmpty()) {
            Message messageToUpdate = messages.getFirst();
            JCFMessageService.update(messageToUpdate.getId(), "안녕하세요 여러분 OOP 개념은 익숙하신가요?");
        }

        // 메시지 삭제
        if (messages.size() > 1) {
            Message messageToDelete = messages.get(1);
            JCFMessageService.delete(messageToDelete.getId());
        }

        // 유저1 삭제
        jcfUserService.delete(user1.getId());

        // 채널 조회 재출력 (변화 확인)
        System.out.println();
        simpleChatService.viewChannel(channel1.getId());

        // 전체 메시지 조회
//        System.out.println("전체 메시지 목록:");
//        for (Message m : JCFMessageService.findAll()) {
//            System.out.println("- " + m);
//        }
    }

    private static void edgeCaseTest(SimpleChatService chatService, JCFUserService userService, JCFChannelService channelService, JCFMessageService messageService) {
        System.out.println("\n=== 엣지 케이스 테스트 시작 ===");

        // 유저 및 채널 기본 생성
        User user1 = new User("중복참가자", 22);
        User user2 = new User("삭제유저", 33);
        userService.create(user1);
        userService.create(user2);

        Channel channel = new Channel("엣지 채널", "예외 케이스용 채널");
        channelService.create(channel);
        chatService.joinChannel(user1.getId(), channel.getId());

        // 이미 참여한 유저가 다시 채널에 참가
        System.out.println("\n[중복 참가 시도]");
        chatService.joinChannel(user1.getId(), channel.getId());

        // 참여하지 않은 유저가 채널에서 퇴장
        System.out.println("\n[참여하지 않은 유저가 leave 시도]");
        chatService.leaveChannel(user2.getId(), channel.getId());

        // 삭제된 유저가 메시지 보내려 할 때
        System.out.println("\n[삭제된 유저가 메시지 전송 시도]");
        userService.delete(user2.getId()); // 소프트 삭제
        chatService.sendMessage(user2.getId(), channel.getId(), "삭제된 유저의 메시지");

        // 삭제된 채널에 메시지 전송 시도
        System.out.println("\n[삭제된 채널에 메시지 전송 시도]");
        channelService.delete(channel.getId());
        chatService.sendMessage(user1.getId(), channel.getId(), "삭제된 채널에 보내는 메시지");

        // 참여하지 않은 유저가 메시지 보내는 경우
        System.out.println("\n[비참여 유저의 메시지 전송]");
        User outsider = new User("비참여자", 20);
        userService.create(outsider);
        chatService.sendMessage(outsider.getId(), channel.getId(), "저는 참여한 적 없는데요?");
    }
}
