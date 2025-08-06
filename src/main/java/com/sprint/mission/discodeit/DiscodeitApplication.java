package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.request.*;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;

@ConfigurationPropertiesScan
@SpringBootApplication
public class DiscodeitApplication {
    static UUID testId = UUID.randomUUID();
    static String testString = "testString";
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscodeitApplication.class);

    static void section(String title) {
        System.out.println("\n------------------------------------------------------------ " + title + " ------------------------------------------------------------\n");
    }

    static void exeCheck(String title, Runnable r) {
        System.out.println("=== " + title + " ===");
        try {
            r.run();
            System.out.println("** [SUCCESS] **");
        } catch (RuntimeException e) {
            System.out.println("** [FAILED] " + userMessage(e) + " **");
            LOGGER.warn("실패 메서드 타이틀: {}", title, e);
        }
    }

    static <T> Optional<T> exeCheck(String title, Supplier<T> s) {
        System.out.println("=== " + title + " ===");
        try {
            T t = s.get();
            System.out.println(t);
            System.out.println("** [SUCCESS] **");
            return Optional.ofNullable(t);
        } catch (RuntimeException e) {
            System.out.println("** [FAILED] " + userMessage(e) + " **");
            LOGGER.warn("실패 메서드 타이틀: {}", title, e);
            return Optional.empty();
        }
    }

    static String userMessage(Throwable e) {
        String msg = e.getMessage();
        return (msg == null || msg.isBlank()) ? "오류가 발생했습니다." : msg;
    }

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
        return messageService.create(messageCreateRequest);
    }

    static Message messageCreateTest(MessageService messageService, Channel channel, User author, String content) {
        MessageCreateRequest messageCreateRequest = new MessageCreateRequest(
                content, channel.getId(), author.getId(), null);
        return messageService.create(messageCreateRequest);
    }

    static void binaryContentTest(BinaryContentService binaryContentService, Message message) {
        section("BinaryContent 테스트");
        Optional<BinaryContent> binaryContentOpt = exeCheck(
                "create : 정상", () -> createBinaryContent(binaryContentService, "images.jpg", "jpg"));
        exeCheck("create : 존재하지 않는 파일", () -> createBinaryContent(binaryContentService, testString, "jpg"));
        exeCheck("create : 잘못된 확장자", () -> createBinaryContent(binaryContentService, "images.jpg", "png"));
        BinaryContent savedContent = binaryContentOpt.orElseThrow(NoSuchElementException::new);

        // 조회
        exeCheck("findById : 정상", () -> System.out.println(binaryContentService.findById(savedContent.getId()).getId()));
        exeCheck("findById : 존재하지 않는 ID", () -> System.out.println(binaryContentService.findById(testId).getId()));
        exeCheck("findAllByIdIn : 정상", () -> binaryContentService.findAllByIdIn(message.getAttachmentIds()).forEach(
                content -> System.out.println(content.getId())));
        exeCheck("findAllByIdIn : 메세지에 첨부파일이 없을 때", () -> binaryContentService.findAllByIdIn(new ArrayList<>()).forEach(
                content -> System.out.println(content.getId())));

        // 삭제
        exeCheck("delete : 정상", () -> binaryContentService.delete(savedContent.getId()));
        exeCheck("delete : 삭제된 content", () -> binaryContentService.delete(savedContent.getId()));
        exeCheck("delete : 존재하지 않는 content", () -> binaryContentService.delete(testId));
    }


    static void userTest(UserService userService, UserStatusService userStatusService, BinaryContentService binaryContentService) {
        section("User 테스트");

        BinaryContent profileImage = createBinaryContent(binaryContentService, "images.jpg", "jpg");

        // 생성
        UserCreateRequest request1 = new UserCreateRequest(
                "test1", "aaa@aaa.com", "1234", false, null);
        UserCreateRequest requestUserNameDup = new UserCreateRequest(
                "test1", "bbb@aaa.com", "1234", true, profileImage.getId());
        UserCreateRequest requestEmailDup = new UserCreateRequest(
                "test2", "aaa@aaa.com", "1234", true, profileImage.getId());
        Optional<User> userOpt = exeCheck("create : 정상", () -> userService.create(request1));
        User user = userOpt.orElseThrow(NoSuchElementException::new);
        exeCheck("create : username 중복", () -> userService.create(requestUserNameDup));
        exeCheck("create : email 중복", () -> userService.create(requestEmailDup));

        // 조회 - UserFindResponse 반환 확인 및 userStatus 생성 확인
        exeCheck("findById : 존재하는 유저", () -> userService.findById(user.getId()));
        exeCheck("findById : 존재하지 않는 유저", () -> userService.findById(testId));
        exeCheck("findAll", () -> userService.findAll().forEach(System.out::println));
        exeCheck("UserStatus 생성 확인", () -> userStatusService.findByUserId(user.getId()));

        // 수정
        exeCheck("수정 전 조회", () -> userService.findById(user.getId()));
        UserUpdateRequest updateRequest = new UserUpdateRequest(
                user.getId(), "updated", "updated@aaa.com"
                , "12345", true, profileImage.getId());
        UserUpdateRequest updateUsernameDup = new UserUpdateRequest(
                user.getId(), "updated", "updated2@aaa.com"
                , "12345", true, profileImage.getId());
        UserUpdateRequest updateEmailDup = new UserUpdateRequest(
                user.getId(), "updated2", "updated@aaa.com"
                , "12345", true, profileImage.getId());
        exeCheck("update : 정상", () -> userService.update(updateRequest));
        exeCheck("update : username 중복", () -> userService.update(updateUsernameDup));
        exeCheck("update : email 중복", () -> userService.update(updateEmailDup));
        exeCheck("수정 후 조회", () -> userService.findById(user.getId()));

        // 삭제
        exeCheck("delete : 정상", () -> userService.delete(user.getId()));
        exeCheck("delete : 삭제된 유저", () -> userService.delete(user.getId()));
        exeCheck("delete : 존재하지 않는 ID", () -> userService.delete(testId));
        exeCheck("delete : UserStatus 삭제 확인", () -> userStatusService.findByUserId(user.getId()));
        exeCheck("delete : BinaryContent 삭제 확인", () -> binaryContentService.findById(profileImage.getId()));
        exeCheck("삭제 후 전체 조회", () -> userService.findAll().forEach(System.out::println));

    }

    static void userStatusTest(UserStatusService userStatusService, User user) {
        section("UserStatus 테스트");
        // 생성
        UserStatusCreateRequest requestDup = new UserStatusCreateRequest(user.getId());
        UserStatusCreateRequest requestUserNotFound = new UserStatusCreateRequest(testId);
        exeCheck("create : userStatus 중복", () -> userStatusService.create(requestDup));
        exeCheck("create : 존재하지 않는 유저", () -> userStatusService.create(requestUserNotFound));

        // 조회
        exeCheck("findAll", () -> userStatusService.findAll().forEach(System.out::println));
        Optional<UserStatus> userStatusOpt = exeCheck("findByUserId : 정상", () -> userStatusService.findByUserId(user.getId()));
        exeCheck("findByUserId : 존재하지 않는 UserId", () -> userStatusService.findByUserId(testId));
        UserStatus userStatus = userStatusOpt.orElseThrow(NoSuchElementException::new);
        exeCheck("findById : 정상", () -> userStatusService.findById(userStatus.getId()));
        exeCheck("findById : 존재하지 않는 ID", () -> userStatusService.findById(testId));

        // 수정
        exeCheck("수정 전 조회", () -> userStatusService.findById(userStatus.getId()));
        UserStatusUpdateRequest updateRequest = new UserStatusUpdateRequest(userStatus.getId(), true);
        UserStatusUpdateRequest updateNotFound = new UserStatusUpdateRequest(testId, false);
        exeCheck("update : 정상", () -> userStatusService.update(updateRequest));
        exeCheck("update : 존재하지 않는 ID", () -> userStatusService.update(updateNotFound));
        exeCheck("updateById 수정 후 조회", () -> userStatusService.findById(userStatus.getId()));
        UserStatusUpdateRequest updateRequest2 = new UserStatusUpdateRequest(user.getId(), false);
        UserStatusUpdateRequest updateUserNotFound = new UserStatusUpdateRequest(testId, false);
        exeCheck("updateByUserId : 정상", () -> userStatusService.updateByUserId(updateRequest2));
        exeCheck("updateByUserId : 존재하지 않는 유저", () -> userStatusService.updateByUserId(updateUserNotFound));
        exeCheck("updateByUserId 수정 후 조회", () -> userStatusService.findById(userStatus.getId()));


        // 삭제
        exeCheck("delete : 정상", () -> userStatusService.delete(userStatus.getId()));
        exeCheck("delete : 존재하지 않는 ID", () -> userStatusService.delete(testId));
        exeCheck("delete : 삭제된 ID", () -> userStatusService.delete(userStatus.getId()));
        exeCheck("삭제 후 전체 조회", () -> userStatusService.findAll().forEach(System.out::println));
    }

    static void channelTest(ChannelService channelService, MessageService messageService
            , ReadStatusService readStatusService, UserService userService) {
        section("Channel 테스트");
        // private 채널에 등록할 유저 리스트 생성
        List<User> users = new ArrayList<>();
        users.add(createUser(userService, "channelUser1", "1234", "channel1@ch.com"));
        users.add(createUser(userService, "channelUser2", "1234", "channel2@ch.com"));
        users.add(createUser(userService, "channelUser3", "1234", "channel3@ch.com"));

        // 생성
        ChannelCreateRequest requestPublic = new ChannelCreateRequest("public", "public 채널입니다.", null);
        ChannelCreateRequest requestNameNull = new ChannelCreateRequest(null, "public 채널입니다.", null);
        ChannelCreateRequest requestNameEmpty = new ChannelCreateRequest("", "public 채널입니다.", null);
        ChannelCreateRequest requestPrivate = new ChannelCreateRequest(null, null, users);

        Optional<Channel> channelPublicOpt = exeCheck("createPublic : 정상", () -> channelService.createPublic(requestPublic));
        exeCheck("createPublic : 채널 이름 null", () -> channelService.createPublic(requestNameNull));
        exeCheck("createPublic : 채널 이름 empty", () -> channelService.createPublic(requestNameEmpty));
        Optional<Channel> channelPrivateOpt = exeCheck("createPrivate", () -> channelService.createPrivate(requestPrivate));
        Channel publicChannel = channelPublicOpt.orElseThrow(NoSuchElementException::new);
        Channel privateChannel = channelPrivateOpt.orElseThrow(NoSuchElementException::new);

        // 채널에 등록할 메세지 생성
        messageCreateTest(messageService, publicChannel, users.get(0), "안녕하세요");
        messageCreateTest(messageService, publicChannel, users.get(1), "안녕하세요");
        messageCreateTest(messageService, privateChannel, users.get(1), "안녕하세요");
        messageCreateTest(messageService, privateChannel, users.get(2), "안녕하세요");

        exeCheck("findAllByChannelId : 메세지 생성 확인, publicChannel"
                , () -> messageService.findAllByChannelId(publicChannel.getId()).forEach(System.out::println));
        exeCheck("findAllByChannelId : 메세지 생성 확인, privateChannel"
                , () -> messageService.findAllByChannelId(privateChannel.getId()).forEach(System.out::println));
        exeCheck("ReadStatus 생성 확인", () -> readStatusService.findAllByUserId(users.get(1).getId()).forEach(System.out::println));

        // 조회
        exeCheck("findById : 정상, public", () -> channelService.findById(publicChannel.getId()));
        exeCheck("findById : 정상, private", () -> channelService.findById(privateChannel.getId()));
        exeCheck("findById : 존재하지 않는 채널", () -> channelService.findById(testId));
        exeCheck("findAllByUserId : 해당하는 유저가 있을 경우"
                , () -> channelService.findAllByUserId(users.get(0).getId()).forEach(System.out::println));
        exeCheck("findAllByUserId : public 채널만 조회됨"
                , () -> channelService.findAllByUserId(testId).forEach(System.out::println));

        // 수정
        ChannelUpdateRequest updatedPublic = new ChannelUpdateRequest(
                publicChannel.getId(), "updatedPublic", "수정된 public 채널입니다.");
        ChannelUpdateRequest updatedPrivate = new ChannelUpdateRequest(
                privateChannel.getId(), "updatedPrivate", "수정된 private 채널입니다.");
        ChannelUpdateRequest updateNotFound = new ChannelUpdateRequest(
                testId, "test", "test");
        ChannelUpdateRequest updateNameNull = new ChannelUpdateRequest(
                publicChannel.getId(), null, "test");
        ChannelUpdateRequest updateNameEmpty = new ChannelUpdateRequest(
                publicChannel.getId(), "", "test");

        exeCheck("수정 전", () -> channelService.findById(publicChannel.getId()));
        exeCheck("update : 정상", () -> channelService.update(updatedPublic));
        exeCheck("update : private, 불가", () -> channelService.update(updatedPrivate));
        exeCheck("update : 존재하지 않는 채널", () -> channelService.update(updateNotFound));
        exeCheck("update : 채널 이름 null", () -> channelService.update(updateNameNull));
        exeCheck("update : 채널 이름 empty", () -> channelService.update(updateNameEmpty));
        exeCheck("수정 후", () -> channelService.findById(publicChannel.getId()));

        // 삭제
        exeCheck("delete : 정상, public", () -> channelService.delete(publicChannel.getId()));
        exeCheck("delete : 정상, private", () -> channelService.delete(privateChannel.getId()));
        exeCheck("delete : 삭제된 채널", () -> channelService.delete(privateChannel.getId()));
        exeCheck("delete : 존재하지 않는 채널", () -> channelService.delete(testId));
        exeCheck("삭제 후 조회", () -> channelService.findAllByUserId(users.get(1).getId()).forEach(System.out::println));
        exeCheck("채널 메세지 삭제 확인, public", () -> System.out.println(messageService.findAllByChannelId(publicChannel.getId()).isEmpty()));
        exeCheck("채널 메세지 삭제 확인, private", () -> System.out.println(messageService.findAllByChannelId(privateChannel.getId()).isEmpty()));
        exeCheck("readStatus 삭제 확인", () -> System.out.println(readStatusService.findAllByUserId(users.get(1).getId()).isEmpty()));
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

    static void readStatusTest(ReadStatusService readStatusService, ChannelService channelService, List<User> users, Channel channel) {
        System.out.println("-----------------------ReadStatus 테스트-----------------------");
        System.out.println();
        // 생성 - 직접 생성하는 객체가 아님
        Channel channel1 = createPrivateChannel(channelService, users, "private1", "description");
        Channel channel2 = createPrivateChannel(channelService, users, "private2", "description");
        Channel channel3 = createPrivateChannel(channelService, users, "private3", "description");
//        ReadStatus readStatus = createReadStatus(readStatusService, user.getId(), channel1.getId()); // 이미 존재하는 객체
//        createReadStatus(readStatusService, testId, channel1.getId()); // 존재하지 않는 채널 또는 유저
//        createReadStatus(readStatusService, user.getId(), testId);
//        createReadStatus(readStatusService, testId, testId);

        // 조회
        ReadStatus readStatus = readStatusService.findAllByUserId(users.get(0).getId()).get(0);
        System.out.println("findById : ");
        System.out.println(readStatusService.findById(readStatus.getId()));
//        readStatusService.findById(testId); // 예외 발생
        System.out.println("findAllByUserId : ");
        readStatusService.findAllByUserId(users.get(0).getId()).forEach(System.out::println);
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

    static Channel createPrivateChannel(ChannelService channelService, List<User> users, String name, String description) {
        ChannelCreateRequest channelCreateRequest = new ChannelCreateRequest(name, description, users);
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
        userStatusTest(userStatusService, user);
        channelTest(channelService, messageService, readStatusService, userService);
//        messageTest(messageService, binaryContentService, user, channel, binaryContent);
//        readStatusTest(readStatusService, channelService, user, channel);
//        authTest(authService, user);

        userService.deleteAll();
        channelService.deleteAll();
        messageService.deleteAll();
        binaryContentService.deleteAll();
        readStatusService.deleteAll();
        userStatusService.deleteAll();
    }
}