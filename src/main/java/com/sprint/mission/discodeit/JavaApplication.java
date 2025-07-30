package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.nio.file.Path;
import java.util.UUID;

public class JavaApplication {

    // File 레포지토리 선언
//    static UserRepository userRepo = new FileUserRepository(Path.of("/Users/apple/dev_source/5-sprint-mission/userDirectory/"));
//    static ChannelRepository channelRepo = new FileChannelRepository(Path.of("/Users/apple/dev_source/5-sprint-mission/channelDirectory/"));
//    static MessageRepository messageRepo = new FileMessageRepository(Path.of("/Users/apple/dev_source/5-sprint-mission/messageDirectory/"));

    // JCF 레포지토리 선언
//    static UserRepository userRepo = new JCFUserRepository();
//    static ChannelRepository channelRepo = new JCFChannelRepository();
//    static MessageRepository messageRepo = new JCFMessageRepository();

//    static UserService userService = new FileUserService(Path.of("/Users/apple/dev_source/5-sprint-mission/userDirectory/"));
//    static ChannelService channelService = new FileChannelService(Path.of("/Users/apple/dev_source/5-sprint-mission/channelDirectory/"));
//    static MessageService messageService = new FileMessageService(Path.of("/Users/apple/dev_source/5-sprint-mission/messageDirectory/"), userService, channelService);

    static UserService userService = new JCFUserService();
    static ChannelService channelService = new JCFChannelService();
    static MessageService messageService = new JCFMessageService(userService, channelService);

//    static UserService userService = new BasicUserService(userRepo);
//    static ChannelService channelService = new BasicChannelService(channelRepo);
//    static MessageService messageService = new BasicMessageService(messageRepo, userService, channelService);

    // 테스트용 uuid, String
    static UUID testId = UUID.randomUUID();
    static String testString = "testString";


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

        channelService.create(new Channel("코드잇 스프린트 백엔드", "Spring 백엔드 학습 채널입니다.", Channel.ChannelType.PUBLIC));
        channelService.create(new Channel("코드잇 스프린트 프론트엔드", "프론트엔드 학습 채널입니다.", Channel.ChannelType.PUBLIC));
        channelService.create(new Channel("스프린트 커뮤니티", "스프린트 소통 채널입니다", Channel.ChannelType.PUBLIC));
        channelService.create(new Channel("마작 공부", "마작에 대해서 배우는 채널입니다.", Channel.ChannelType.PRIVATE));

        messageService.create(new Message("안녕하세요?", userService.searchAll().get(0).getId(), channelService.searchAll().get(0).getId()));
        messageService.create(new Message("안녕하시렵니까?", userService.searchAll().get(0).getId(), channelService.searchAll().get(0).getId()));
        messageService.create(new Message("다음에 뵙겠습니다.", userService.searchAll().get(1).getId(), channelService.searchAll().get(1).getId()));
        messageService.create(new Message("기체후 일향 만강 하옵니까?", userService.searchAll().get(1).getId(), channelService.searchAll().get(1).getId()));

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
//        System.out.println(userService.searchById(testId)); // 조회 오류 테스트
        System.out.println("searchByName(\"홍\") 검색 결과");
        userService.searchByName("홍").forEach(System.out::println);
//        userService.searchByName(testString).forEach(System.out::println); // 조회 오류 테스트 2

        // 수정
        System.out.println("수정 전 : " + userService.searchById(user1.getId()));
        userService.updateName(user1.getId(), "updatedName");
//        userService.updateName(testId, "updatedName"); // 수정 오류 테스트
        System.out.println("수정 후 : " + userService.searchById(user1.getId()));

        // 삭제
        userService.delete(user1.getId());
//        userService.delete(testId); // 삭제 오류 테스트
        userService.searchAll().forEach(System.out::println);
    }

    static void testChannelService() {
        System.out.println("-------------------------------------------- 채널 서비스 테스트 --------------------------------------------");
        System.out.println("------------전체 목록------------");
        channelService.searchAll().forEach(System.out::println);
        System.out.println("-------------------------------");

        // 등록
        Channel channel1 = new Channel("test", "testDescription", Channel.ChannelType.PUBLIC);
        channelService.create(channel1);

        // 조회
        System.out.println(channelService.searchById(channel1.getId()));
//        System.out.println(channelService.searchById(testId)); // 조회 오류 테스트
        System.out.println("searchByName(\"코드잇\") 검색 결과");
        channelService.searchByName("코드잇").forEach(System.out::println);
//        channelService.searchByName(testString).forEach(System.out::println); // 조회 오류 테스트

        // 수정 - 확인
        System.out.println("수정 전 : " + channelService.searchById(channel1.getId()));
        channelService.updateName(channel1.getId(), "updatedName");
        channelService.updateDescription(channel1.getId(), "updatedDescription");
        channelService.updateChannelType(channel1.getId(), Channel.ChannelType.PRIVATE);
//        channelService.updateName(testId, "updatedName2"); // 수정 오류 테스트
//        channelService.updateDescription(testId, "updatedDescription2");
//        channelService.updateChannelType(testId, Channel.ChannelType.PUBLIC);
        System.out.println("수정 후 : " + channelService.searchById(channel1.getId()));

        // 삭제 - 확인
        channelService.delete(channel1.getId());
//        channelService.delete(testId); // 삭제 오류 테스트
        channelService.searchAll().forEach(System.out::println);
    }

    static void testMessageService() {
        System.out.println("-------------------------------------------- 메세지 서비스 테스트 --------------------------------------------");
        System.out.println("------------전체 목록------------");
        messageService.searchAll().forEach(System.out::println);
        System.out.println("-------------------------------");

        // 등록
        Message message1 = new Message("test message1", userService.searchAll().get(0).getId(), channelService.searchAll().get(0).getId());
        messageService.create(message1);

        // 조회
        System.out.println(messageService.searchById(message1.getId()));
//        System.out.println(messageService.searchById(testId)); // 조회 오류 테스트
        System.out.println("searchByContent(\"안녕\") 검색 결과");
        messageService.searchByContent("안녕").forEach(System.out::println);
        System.out.println("searchBySenderId() 검색 결과");
        messageService.searchBySenderId(userService.searchAll().get(0).getId()).forEach(System.out::println);
//        messageService.searchByContent(testString).forEach(System.out::println); // 조회 오류 테스트2
//        messageService.searchBySenderId(testId).forEach(System.out::println);

        // 수정 - 확인
        System.out.println("수정 전 : " + messageService.searchById(message1.getId()));
        messageService.updateContent(message1.getId(), "updatedContent");
        messageService.updateSenderId(message1.getId(), userService.searchAll().get(1).getId());
        messageService.updateChannelId(message1.getId(), channelService.searchAll().get(1).getId());
//        messageService.updateContent(testId, testString); // 수정 오류 테스트
//        messageService.updateSenderId(testId, testId);
//        messageService.updateChannelId(testId, testId);
        System.out.println("수정 후 : " + messageService.searchById(message1.getId()));

        // 삭제 - 확인
        messageService.delete(message1.getId());
//        messageService.delete(testId); // 삭제 오류 테스트
        messageService.searchAll().forEach(System.out::println);
    }

}
