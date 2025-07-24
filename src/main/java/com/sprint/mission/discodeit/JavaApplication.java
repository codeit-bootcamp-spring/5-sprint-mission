package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.nio.file.Path;
import java.util.UUID;

public class JavaApplication {

    static UserService userService = new FileUserService(Path.of("/Users/apple/dev_source/5-sprint-mission/userDirectory/"));
    static ChannelService channelService = new FileChannelService(Path.of("/Users/apple/dev_source/5-sprint-mission/channelDirectory/"));
    static MessageService messageService = new FileMessageService(Path.of("/Users/apple/dev_source/5-sprint-mission/messageDirectory/"));

    public static void main(String[] args) {

        // 초기화
        userService.deleteAll();
        channelService.deleteAll();
        messageService.deleteAll();

        // 인스턴스 생성
        userService.create(new User("홍길동"));
        userService.create(new User("홍길순"));
        userService.create(new User("박길동"));
        userService.create(new User("김길동"));

        channelService.create(new Channel("코드잇 스프린트 백엔드"));
        channelService.create(new Channel("코드잇 스프린트 프론트엔드"));
        channelService.create(new Channel("스프린트 커뮤니티"));
        channelService.create(new Channel("마작 공부"));

        messageService.create(new Message("안녕하세요?", userService.searchAll().get(0).getId()));
        messageService.create(new Message("안녕하시렵니까?", userService.searchAll().get(0).getId()));
        messageService.create(new Message("다음에 뵙겠습니다.", userService.searchAll().get(1).getId()));
        messageService.create(new Message("기체후 일향 만강 하옵니까?", userService.searchAll().get(1).getId()));

        testUserService();
        testChannelService();
        testMessageService();
    }

    static void testUserService() {
        System.out.println("-------------------------------------------- 유저 서비스 테스트 --------------------------------------------");
        System.out.println("------------전체 목록------------");
        userService.searchAll().forEach(System.out::println);
        System.out.println("-------------------------------");

        // 등록
        User user1 = new User("test1");
        userService.create(user1);

        // 조회
        System.out.println(userService.searchById(user1.getId()));
        System.out.println("searchByName(\"홍\") 검색 결과");
        userService.searchByName("홍").forEach(System.out::println);

        // 수정 - 확인
        System.out.println("수정 전 : " + userService.searchById(user1.getId()));
        userService.update(user1.updateName("updatedName"));
        userService.update(user1.updateName("updatedName"));
        userService.update(user1.addChannel(channelService.searchAll().get(1)));
        userService.update(user1.addChannel(channelService.searchAll().get(1)));
        System.out.println("수정 후 : " + userService.searchById(user1.getId()));

        // 삭제 - 확인
        userService.delete(user1);
        System.out.println(userService.searchById(user1.getId()));

    }

    static void testChannelService() {
        System.out.println("-------------------------------------------- 채널 서비스 테스트 --------------------------------------------");
        System.out.println("------------전체 목록------------");
        channelService.searchAll().forEach(System.out::println);
        System.out.println("-------------------------------");

        // 등록
        Channel channel1 = new Channel("test1");
        channelService.create(channel1);

        // 조회
        System.out.println(channelService.searchById(channel1.getId()));
        System.out.println("searchByName(\"코드잇\") 검색 결과");
        channelService.searchByName("코드잇").forEach(System.out::println);

        // 수정 - 확인
        System.out.println("수정 전 : " + channelService.searchById(channel1.getId()));
        channelService.update(channel1.updateName("updatedName"));
        channelService.update(channel1.updateName("updatedName"));
        channelService.update(channel1.addUser(userService.searchAll().get(0)));
        channelService.update(channel1.addUser(userService.searchAll().get(0)));
        System.out.println("수정 후 : " + channelService.searchById(channel1.getId()));

        // 삭제 - 확인
        channelService.delete(channel1);
        System.out.println(channelService.searchById(channel1.getId()));

    }

    static void testMessageService() {
        System.out.println("-------------------------------------------- 메세지 서비스 테스트 --------------------------------------------");
        System.out.println("------------전체 목록------------");
        messageService.searchAll().forEach(System.out::println);
        System.out.println("-------------------------------");

        // 등록
        Message message1 = new Message("test message1", userService.searchAll().get(0).getId());
        messageService.create(message1);

        // 조회
        System.out.println(messageService.searchById(message1.getId()));
        System.out.println("searchByContent(\"안녕\") 검색 결과");
        messageService.searchByContent("안녕").forEach(System.out::println);
        System.out.println("searchBySenderId() 검색 결과");
        messageService.searchBySenderId(userService.searchAll().get(0).getId()).forEach(System.out::println);

        // 수정
        System.out.println("수정 전 : " + messageService.searchById(message1.getId()));
        messageService.update(message1.updateContent("updated content"));
        messageService.update(message1.updateContent("updated content"));
        messageService.update(message1.updateSender(userService.searchAll().get(1).getId()));
        messageService.update(message1.updateSender(userService.searchAll().get(1).getId()));
        System.out.println("수정 후 : " + messageService.searchById(message1.getId()));

        // 삭제
        messageService.delete(message1);

        // 조회
        System.out.println(messageService.searchById(message1.getId()));

    }

}
