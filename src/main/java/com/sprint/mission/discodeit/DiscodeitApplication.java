package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.request.*;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ConfigurationPropertiesScan
@SpringBootApplication
public class DiscodeitApplication {
    static UUID testId = UUID.randomUUID();
    static String testString = "testString";

    static User createUser(UserService userService, String username, String password, String email) {
        UserCreateRequest userCreateRequest = new UserCreateRequest(
                username, email, password, false, null);

        return userService.create(userCreateRequest);
    }

    static User createUser(UserService userService, String username, String password, String email, UUID profileId) {
        UserCreateRequest userCreateRequest = new UserCreateRequest(
                username, email, password, true, profileId);

        return userService.create(userCreateRequest);
    }

    static Channel setupChannel(ChannelService channelService) {
        ChannelCreateRequest channelCreateRequest = new ChannelCreateRequest(
                "공지", "공지 채널입니다.", null);

        return channelService.createPublic(channelCreateRequest);
    }

    static BinaryContent createBinaryContent(BinaryContentService binaryContentService, String path, String contentType) {
        byte[] bytes;
        try {
            Path imagePath = Path.of(System.getProperty("user.dir"), path);
            bytes = Files.readAllBytes(imagePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        BinaryContentCreateRequest request = new BinaryContentCreateRequest(
                path, contentType, (long) bytes.length, bytes);

        return binaryContentService.create(request);
    }

    static Message messageCreateTest(MessageService messageService, Channel channel, User author, BinaryContent binaryContent, String content) {
        List<UUID> attachmentIds = new ArrayList<>();
        attachmentIds.add(binaryContent.getId());
        MessageCreateRequest messageCreateRequest = new MessageCreateRequest(
                content, channel.getId(), author.getId(), attachmentIds);
        Message message = messageService.create(messageCreateRequest);
        return message;
    }

    static Message messageCreateTest(MessageService messageService, Channel channel, User author, String content) {
        MessageCreateRequest messageCreateRequest = new MessageCreateRequest(
                content, channel.getId(), author.getId(), null);
        Message message = messageService.create(messageCreateRequest);
        return message;
    }

    static void binaryContentTest(BinaryContentService binaryContentService, Message message) {
        System.out.println("-----------------------BinaryContent 테스트-----------------------");
        System.out.println();
        BinaryContent savedContent = createBinaryContent(binaryContentService, "images.jpg", "jpg");

        // 조회
        System.out.print("findById : ");
        System.out.println(binaryContentService.findById(savedContent.getId()).getId());
        System.out.print("findAllByIdIn : ");
        binaryContentService.findAllByIdIn(message.getAttachmentIds()).forEach(
                content -> System.out.println(content.getId()));

        // 삭제
//        binaryContentService.delete(UUID.randomUUID()); // 예외 발생 확인
        binaryContentService.delete(savedContent.getId());
//        System.out.println(binaryContentService.findById(savedContent.getId())); // 예외 발생 확인
    }


    static void userTest(UserService userService, UserStatusService userStatusService, BinaryContentService binaryContentService) {
        System.out.println("-----------------------User 테스트-----------------------");
        System.out.println();

        BinaryContent profileImage = createBinaryContent(binaryContentService, "images.jpg", "jpg");
        // 생성
        UserCreateRequest userCreateRequest = new UserCreateRequest(
                "test1", "aaa@aaa.com", "1234", false, null);
        UserCreateRequest userCreateRequest2 = new UserCreateRequest(
                "test1", "bbb@aaa.com", "1234", true, profileImage.getId());
        UserCreateRequest userCreateRequest3 = new UserCreateRequest(
                "test2", "aaa@aaa.com", "1234", true, profileImage.getId());
        User user = userService.create(userCreateRequest);
//        User user2 = userService.create(userCreateRequest2); // username 중복 체크 테스트
//        User user3 = userService.create(userCreateRequest3); // email 중복 체크 테스트


        // 조회 - UserFindResponse 반환 확인 및 userStatus 생성 확인
        System.out.println("findById : ");
        System.out.println(userService.findById(user.getId()));
//        userService.findById(testId); // 예외 발생 확인
        System.out.println("findAll : ");
        userService.findAll().forEach(System.out::println);
        System.out.println("UserStatus : ");
        System.out.println(userStatusService.findByUserId(user.getId()));

        // 수정
        System.out.println("수정 전 : ");
        System.out.println(userService.findById(user.getId()));
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest(
                user.getId(), "updated", "updated@aaa.com"
                , "12345", true, profileImage.getId());
        UserUpdateRequest userUpdateRequest2 = new UserUpdateRequest(
                user.getId(), "updated", "updated2@aaa.com"
                , "12345", true, profileImage.getId());
        UserUpdateRequest userUpdateRequest3 = new UserUpdateRequest(
                user.getId(), "updated2", "updated@aaa.com"
                , "12345", true, profileImage.getId());
        userService.update(userUpdateRequest);
//        userService.update(userUpdateRequest2); // username 중복 체크 테스트
//        userService.update(userUpdateRequest3); // email 중복 체크 테스트
        System.out.println("수정 후 : ");
        System.out.println(userService.findById(user.getId()));

        // 삭제
        userService.delete(user.getId());
//        System.out.println(userStatusService.findByUserId(user.getId())); // UserStatus 삭제 확인
//        System.out.println(binaryContentService.findById(profileImage.getId())); // BinaryContent 삭제 확인
//        System.out.println(userService.findById(user.getId())); // User 삭제 확인
//        userService.delete(user.getId()); // 삭제 예외 처리 확인
        System.out.println("삭제 후 전체 조회 : ");
        userService.findAll().forEach(System.out::println);
    }

    static void userStatusTest(UserStatusService userStatusService, User user) {
        System.out.println("-----------------------UserStatus 테스트-----------------------");
        System.out.println();
        // 생성
//        UserStatusCreateRequest request = new UserStatusCreateRequest(user.getId());
//        UserStatusCreateRequest request2 = new UserStatusCreateRequest(UUID.randomUUID());
//        userStatusService.create(request); // 이미 존재하는 유저의 Status 생성 테스트
//        userStatusService.create(request2); // 존재하지 않는 유저의 Status 생성 테스트

        // 조회
        System.out.println("전체 조회");
        userStatusService.findAll().forEach(System.out::println);
        System.out.println("findByUserId : ");
        UserStatus userStatus = userStatusService.findByUserId(user.getId());
        System.out.println(userStatus);
        System.out.println("findById : ");
        System.out.println(userStatusService.findById(userStatus.getId()));

        // 수정
        System.out.println("수정 전 : ");
        System.out.println(userStatusService.findById(userStatus.getId()).isLoginStatus());
        UserStatusUpdateRequest userStatusUpdateRequest = new UserStatusUpdateRequest(userStatus.getId(), true);
        userStatusService.update(userStatusUpdateRequest);
        System.out.println("update 수정 후 : ");
        System.out.println(userStatusService.findById(userStatus.getId()).isLoginStatus());
        UserStatusUpdateRequest userStatusUpdateRequest2 = new UserStatusUpdateRequest(user.getId(), false);
        userStatusService.updateByUserId(userStatusUpdateRequest2);
        System.out.println("updateByUserId 수정 후 : ");
        System.out.println(userStatusService.findById(userStatus.getId()).isLoginStatus());


        // 삭제
        userStatusService.delete(userStatus.getId());
        System.out.println("삭제 후 전체 조회 : ");
        userStatusService.findAll().forEach(System.out::println);

    }

    static void channelTest(ChannelService channelService, MessageService messageService, ReadStatusService readStatusService, User user) {
        System.out.println("-----------------------Channel 테스트-----------------------");
        System.out.println();
        // 생성
        ChannelCreateRequest channelCreateRequest = new ChannelCreateRequest(
                "public", "public 채널입니다.", null);
        ChannelCreateRequest channelCreateRequest2 = new ChannelCreateRequest(
                null, null, user);
        Channel publicChannel = channelService.createPublic(channelCreateRequest);
        Channel privateChannel = channelService.createPrivate(channelCreateRequest2);
        messageCreateTest(messageService, privateChannel, user, "안녕하세요");
        messageCreateTest(messageService, privateChannel, user, "안녕하세요");
        messageCreateTest(messageService, publicChannel, user, "안녕하세요");
        messageCreateTest(messageService, publicChannel, user, "안녕하세요");
        messageService.findAllByChannelId(privateChannel.getId()).forEach(System.out::println);

        // 조회
        System.out.println("findById : ");
        System.out.println(channelService.findById(privateChannel.getId()));
        System.out.println(channelService.findById(publicChannel.getId()));
//        System.out.println(channelService.findById(testId)); // 조회 오류 테스트
        System.out.println("findAllByUserId : ");
        channelService.findAllByUserId(user.getId()).forEach(System.out::println);
        System.out.println("public 채널만 조회");
        channelService.findAllByUserId(testId).forEach(System.out::println); // public 채널만 나와야 함

        // 수정
        ChannelUpdateRequest channelUpdateRequest = new ChannelUpdateRequest(
                publicChannel.getId(), "updatedPublic", "수정된 public 채널입니다.");
        ChannelUpdateRequest channelUpdateRequest2 = new ChannelUpdateRequest(
                privateChannel.getId(), "updatedPrivate", "수정된 private 채널입니다.");
        ChannelUpdateRequest channelUpdateRequest3 = new ChannelUpdateRequest(
                testId, "test", "test");

        System.out.println("수정 전 : ");
        System.out.println(channelService.findById(publicChannel.getId()));
        channelService.update(channelUpdateRequest);
//        channelService.update(channelUpdateRequest2); // private 채널 업데이트 오류
//        channelService.update(channelUpdateRequest3); // 존재하지 않는 채널 업데이트 오류
        System.out.println("수정 후 : ");
        System.out.println(channelService.findById(publicChannel.getId()));

        // 삭제
        channelService.delete(publicChannel.getId());
        channelService.delete(privateChannel.getId());
        System.out.println("삭제 후 조회 : ");
        channelService.findAllByUserId(user.getId()).forEach(System.out::println);
        System.out.println(messageService.findAllByChannelId(privateChannel.getId()).isEmpty()); // 채널 메세지 삭제 확인
        System.out.println(messageService.findAllByChannelId(publicChannel.getId()).isEmpty());  // -> 삭제됐을시 true
        System.out.println(readStatusService.findAllByUserId(user.getId()).isEmpty()); // readStatus 삭제 확인
    }

    static void messageTest(MessageService messageService, BinaryContentService binaryContentService, User user, Channel channel, BinaryContent binaryContent) {
        System.out.println("-----------------------Message 테스트-----------------------");
        System.out.println();
        // 생성
        Message message = messageCreateTest(messageService, channel, user, binaryContent, "test1");
        messageCreateTest(messageService, channel, user, "test2");
        messageCreateTest(messageService, channel, user, "test3");
        messageCreateTest(messageService, channel, user, "test4");

        // 조회
        System.out.println("findById : ");
        System.out.println(messageService.find(message.getId()));
//        messageService.find(testId); // 존재하지 않는 id 예외 발생
        System.out.println("findAllByChannelId : ");
        messageService.findAllByChannelId(channel.getId()).forEach(System.out::println);
        System.out.println(messageService.findAllByChannelId(testId).isEmpty()); // 정상 작동 시 true

        // 수정
        System.out.println("수정 전 : ");
        System.out.println(messageService.find(message.getId()).getContent());
        MessageUpdateRequest messageUpdateRequest = new MessageUpdateRequest(message.getId(), "updatedContent");
        MessageUpdateRequest messageUpdateRequest2 = new MessageUpdateRequest(testId, "updatedContent");
        messageService.update(messageUpdateRequest);
//        messageService.update(messageUpdateRequest2); // 존재하지 않는 메세지 예외 발생
        System.out.println("수정 후 : ");
        System.out.println(messageService.find(message.getId()).getContent());

        // 삭제
        messageService.delete(message.getId());
//        messageService.delete(message.getId()); // 삭제 및 예외 발생
//        binaryContentService.findById(binaryContent.getId()); // 관련된 도메인 삭제
    }

    static void readStatusTest(ReadStatusService readStatusService, ChannelService channelService, User user, Channel channel) {
        System.out.println("-----------------------ReadStatus 테스트-----------------------");
        System.out.println();
        // 생성 - 직접 생성하는 객체가 아님
        Channel channel1 = createPrivateChannel(channelService, user, "private1", "description");
        Channel channel2 = createPrivateChannel(channelService, user, "private2", "description");
        Channel channel3 = createPrivateChannel(channelService, user, "private3", "description");
//        ReadStatus readStatus = createReadStatus(readStatusService, user.getId(), channel1.getId()); // 이미 존재하는 객체
//        createReadStatus(readStatusService, testId, channel1.getId()); // 존재하지 않는 채널 또는 유저
//        createReadStatus(readStatusService, user.getId(), testId);
//        createReadStatus(readStatusService, testId, testId);

        // 조회
        ReadStatus readStatus = readStatusService.findAllByUserId(user.getId()).get(0);
        System.out.println("findById : ");
        System.out.println(readStatusService.findById(readStatus.getId()));
//        readStatusService.findById(testId); // 예외 발생
        System.out.println("findAllByUserId : ");
        readStatusService.findAllByUserId(user.getId()).forEach(System.out::println);
        System.out.println(readStatusService.findAllByUserId(testId).isEmpty()); // 정상작동시 true 반환

        // 수정
        ReadStatusUpdateRequest readStatusUpdateRequest = new ReadStatusUpdateRequest(readStatus.getId(), true);
        ReadStatusUpdateRequest readStatusUpdateRequest2 = new ReadStatusUpdateRequest(testId, true);
        System.out.println("수정 전 : ");
        System.out.println(readStatusService.findById(readStatus.getId()));
        readStatusService.update(readStatusUpdateRequest);
//        readStatusService.update(readStatusUpdateRequest2); // 예외 발생
        System.out.println("수정 후 : ");
        System.out.println(readStatusService.findById(readStatus.getId()));

        // 삭제
        readStatusService.delete(readStatus.getId());
//        readStatusService.delete(readStatus.getId()); // 예외 발생
    }

    static void authTest(AuthService authService, User user) {
        System.out.println("-----------------------ReadStatus 테스트-----------------------");
        System.out.println();

        UserLoginRequest userLoginRequest1 = new UserLoginRequest(user.getUsername(), user.getPassword());
        UserLoginRequest userLoginRequest2 = new UserLoginRequest(testString, user.getPassword());
        UserLoginRequest userLoginRequest3 = new UserLoginRequest(user.getUsername(), testString);
        UserLoginRequest userLoginRequest4 = new UserLoginRequest(testString, testString);

        System.out.println(authService.login(userLoginRequest1));
//        System.out.println(authService.login(userLoginRequest2)); // 매치 오류
//        System.out.println(authService.login(userLoginRequest3));
//        System.out.println(authService.login(userLoginRequest4));

    }

    static ReadStatus createReadStatus(ReadStatusService readStatusService, UUID userId, UUID channelId) {
        ReadStatusCreateRequest readStatusCreateRequest = new ReadStatusCreateRequest(userId, channelId);
        return readStatusService.create(readStatusCreateRequest);
    }

    static Channel createPrivateChannel(ChannelService channelService, User user, String name, String description) {
        ChannelCreateRequest channelCreateRequest = new ChannelCreateRequest(name, description, user);
        return channelService.createPrivate(channelCreateRequest);
    }


    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

        UserService userService = (UserService) context.getBean("userService");
        ChannelService channelService = (ChannelService) context.getBean("channelService");
        MessageService messageService = (MessageService) context.getBean("messageService");
        BinaryContentService binaryContentService = (BinaryContentService) context.getBean("binaryContentService");
        ReadStatusService readStatusService = (ReadStatusService) context.getBean("readStatusService");
        UserStatusService userStatusService = (UserStatusService) context.getBean("userStatusService");
        AuthService authService = (AuthService) context.getBean("authService");

        userService.deleteAll();
        channelService.deleteAll();
        messageService.deleteAll();
        binaryContentService.deleteAll();
        readStatusService.deleteAll();
        userStatusService.deleteAll();

        User user = createUser(userService, "User in the main", "1234", "inthemain@aaa.com");
        Channel channel = setupChannel(channelService);
        BinaryContent binaryContent = createBinaryContent(binaryContentService, "images.jpg", "jpg");
        BinaryContent profileImage = createBinaryContent(binaryContentService, "images.jpg", "jpg");
        Message message = messageCreateTest(messageService, channel, user, binaryContent, "안녕하세요.");

        binaryContentTest(binaryContentService, message);
        userTest(userService, userStatusService, binaryContentService);
//        userStatusTest(userStatusService, user);
//        channelTest(channelService, messageService, readStatusService, user);
//        messageTest(messageService, binaryContentService, user, channel, binaryContent);
//        readStatusTest(readStatusService, channelService, user, channel);
//        authTest(authService, user);
    }

}