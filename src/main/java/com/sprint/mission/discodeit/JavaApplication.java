package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.UUID;

public class JavaApplication {
    static UserService userService = new JCFUserService();
    static MessageService messageService = new JCFMessageService();
    static ChannelService channelService = new JCFChannelService();

    public static void main(String[] args) {
        // Test 메서드 3종류 실행
        testUserService();
        testMessageService();
        testChannelService();
    }

    public static void testUserService(){ // Test1, 임의의 User 등록
        System.out.println("======= UserService Test =======");

        System.out.println("1) User 등록");
        User user1 = userService.register("홍길동", "1234");
        User user2 = userService.register("박길동", "1357");
        User user3 = userService.register("최길동", "9876");
        User user4 = userService.register("정길동", "1470");

        System.out.println("\n2) User 조회");
        User testUser1 = userService.find(user1.getId()); // user1의 UUID id를 찾아 대입
        findResult(testUser1);
        User testUser2 = userService.find(UUID.randomUUID()); // 랜덤 대입 (중복확인)
        findResult(testUser2);


        System.out.println("\n3) User 전체 조회");
        userService.findAll();

        System.out.println("\n4) User 비밀번호 수정 확인");
        User testUser3 = userService.update(user2.getId(), "0000"); // 초기화 1
        updateResult(testUser3);
        User testUser4 = userService.update(user3.getId(), ""); // 초기화 2
        updateResult(testUser4);

        System.out.println("\n5) User 삭제 및 확인");
        boolean testDelete1 = userService.delete(user4.getId());
        deleteResult(testDelete1);
        boolean testDelete2 = userService.delete(UUID.randomUUID());
        deleteResult(testDelete2);
    }

    public static void testMessageService(){ // Test2,
        System.out.println("======= MessageService Test =======");

        System.out.println("\n1) Message 생성");
        Message message1 = messageService.sendMessage("Python");
        Message message2 = messageService.sendMessage("C++");
        Message message3 = messageService.sendMessage("Java");
        Message message4 = messageService.sendMessage("JavaScript");

        System.out.println("\n2) Message 조회");
        Message testMessage1 = messageService.find(message1.getId());
        findResult(testMessage1);
        Message testMessage2 = messageService.find(UUID.randomUUID());
        findResult(testMessage2);

        System.out.println("\n3) Message 전체 조회");
        messageService.findAll();

        System.out.println("\n4) Message 수정 확인");
        Message testMessage3 = messageService.update(message2.getId(),"C#");
        updateResult(testMessage3);
        Message testMessage4 = messageService.update(message3.getId(),"");
        updateResult(testMessage4);

        System.out.println("\n5) Message 삭제 및 확인");
        boolean testDelete1 = messageService.delete(message4.getId());
        deleteResult(testDelete1);
        boolean testDelete2 = messageService.delete(UUID.randomUUID());
        deleteResult(testDelete2);
    }

    public static void testChannelService(){ // Test3,
        System.out.println("======= ChannelService Test =======");

        System.out.println("\n1) Channel 생성");
        Channel channel1 = channelService.createChannel("공지", "채널 공지가 게시되는 채널입니다.", 1);
        Channel channel2 = channelService.createChannel("채팅", "자유롭게 메시지 채팅이 가능한 채널입니다.", 1);
        Channel channel3 = channelService.createChannel("질문", "운영자에게 질문을 남기는 채널입니다.", 1);
        Channel channel4 = channelService.createChannel("음성 채팅1", "마이크를 사용해 음성 채팅이 가능한 채널입니다.", 2);

        System.out.println("\n2) Channel 조회");
        Channel testChannel1 = channelService.find(channel1.getId());
        findResult(testChannel1);
        Channel testChannel2 = channelService.find(UUID.randomUUID());
        findResult(testChannel2);

        System.out.println("\n3) Channel 전체 조회");
        channelService.findAll();

        System.out.println("\n4) Channel 정보 수정");
        // ChannelName 수정
        Channel testChannel3 = channelService.updateChannelName(channel2.getId(), "대화");
        updateResult(testChannel3);

        // ChannelIntroduction 수정
        Channel testChannel4 = channelService.updateChannelIntroduction(channel3.getId(), "채널을 무기한 폐쇄합니다.");
        updateResult(testChannel4);

        System.out.println("\n5) Channel 삭제 및 확인");
        boolean testDelete1 = channelService.delete(channel4.getId());
        deleteResult(testDelete1);
        boolean testDelete2 = channelService.delete(UUID.randomUUID());
        deleteResult(testDelete2);
    }

    public static <T> void findResult (T result) { // 조회 결과 출력
        if (result instanceof User || result instanceof Message || result instanceof Channel) {
            System.out.println(result);
        } else {
            System.out.println("일치하는 정보를 찾을 수 없습니다.");
        }
    }

    public static <T> void updateResult (T result) { // 수정 결과 출력
        if (result instanceof User || result instanceof Message || result instanceof Channel) {
            System.out.println("수정이 완료되었습니다.");
            System.out.println(result);
        } else {
            System.out.println("일치하는 정보가 없어 수정에 실패했습니다.");
        }
    }

    public static void deleteResult (boolean result) { // 삭제 결과 출력
        if (result) {
            System.out.println("삭제가 완료되었습니다.");
        } else {
            System.out.println("일치하는 정보가 없어 삭제에 실패했습니다.");
        }
    }
}