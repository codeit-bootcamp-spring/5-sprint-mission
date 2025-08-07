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

    static Channel createPrivateChannel(ChannelService channelService, List<User> users, String name, String description) {
        ChannelCreateRequest channelCreateRequest = new ChannelCreateRequest(name, description, users);
        return channelService.createPrivate(channelCreateRequest);
    }

    static Channel createPublicChannel(ChannelService channelService, String name, String description) {
        ChannelCreateRequest channelCreateRequest = new ChannelCreateRequest(name, description, null);
        return channelService.createPublic(channelCreateRequest);
    }

    static Message createMessage(MessageService messageService, UUID channelId, UUID authorId, List<UUID> attachmentIds, String content) {
        MessageCreateRequest messageCreateRequest = new MessageCreateRequest(
                content, channelId, authorId, attachmentIds);
        return messageService.create(messageCreateRequest);
    }

    static Message createMessage(MessageService messageService, UUID channelId, UUID authorId, String content) {
        MessageCreateRequest messageCreateRequest = new MessageCreateRequest(
                content, channelId, authorId, null);
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
        UserCreateRequest request1 = new UserCreateRequest("test1", "aaa@aaa.com", "1234", false, null);
        UserCreateRequest requestUserNameDup = new UserCreateRequest("test1", "bbb@aaa.com", "1234", true, profileImage.getId());
        UserCreateRequest requestEmailDup = new UserCreateRequest("test2", "aaa@aaa.com", "1234", true, profileImage.getId());
        UserCreateRequest requestUsernameEmpty = new UserCreateRequest("", "test@test.com", "1234", false, null);
        UserCreateRequest requestUsernameNull = new UserCreateRequest(null, "test@test.com", "1234", false, null);
        UserCreateRequest requestEmailEmpty = new UserCreateRequest("test", "", "1234", false, null);
        UserCreateRequest requestEmailNull = new UserCreateRequest("test", null, "1234", false, null);
        UserCreateRequest requestEmailFormDiff = new UserCreateRequest("test", "test.com", "1234", false, null);
        UserCreateRequest requestPasswordEmpty = new UserCreateRequest("test", "test@test.com", "", false, null);
        UserCreateRequest requestPasswordNull = new UserCreateRequest("test", "test@test.com", null, false, null);

        Optional<User> userOpt = exeCheck("create : 정상", () -> userService.create(request1));
        User user = userOpt.orElseThrow(NoSuchElementException::new);

        exeCheck("create : username 중복", () -> userService.create(requestUserNameDup));
        exeCheck("create : email 중복", () -> userService.create(requestEmailDup));
        exeCheck("create : username 공백", () -> userService.create(requestUsernameEmpty));
        exeCheck("create : username null", () -> userService.create(requestUsernameNull));
        exeCheck("create : email 공백", () -> userService.create(requestEmailEmpty));
        exeCheck("create : email null", () -> userService.create(requestEmailNull));
        exeCheck("create : email 잘못된 형식", () -> userService.create(requestEmailFormDiff));
        exeCheck("create : password 공백", () -> userService.create(requestPasswordEmpty));
        exeCheck("create : password null", () -> userService.create(requestPasswordNull));

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
        UserUpdateRequest updateEmailDup = new UserUpdateRequest(user.getId(), "updated2", "updated@aaa.com", "12345", true, profileImage.getId());
        UserUpdateRequest updateUsernameEmpty = new UserUpdateRequest(user.getId(), "", "updated@aaa.com", "12345", true, profileImage.getId());
        UserUpdateRequest updateUsernameNull = new UserUpdateRequest(user.getId(), null, "updated@aaa.com", "12345", true, profileImage.getId());
        UserUpdateRequest updateEmailEmpty = new UserUpdateRequest(user.getId(), "updated", "", "12345", true, profileImage.getId());
        UserUpdateRequest updateEmailNull = new UserUpdateRequest(user.getId(), "updated", null, "12345", true, profileImage.getId());
        UserUpdateRequest updateEmailFormDiff = new UserUpdateRequest(user.getId(), "updated", "updated.com", "12345", true, profileImage.getId());
        UserUpdateRequest updatePasswordEmpty = new UserUpdateRequest(user.getId(), "updated", "updated@aaa.com", "", true, profileImage.getId());
        UserUpdateRequest updatePasswordNull = new UserUpdateRequest(user.getId(), "updated", "updated@aaa.com", null, true, profileImage.getId());
        exeCheck("update : 정상", () -> userService.update(updateRequest));
        exeCheck("update : username 중복", () -> userService.update(updateUsernameDup));
        exeCheck("update : email 중복", () -> userService.update(updateEmailDup));
        exeCheck("update : username 공백", () -> userService.update(updateUsernameEmpty));
        exeCheck("update : username null", () -> userService.update(updateUsernameNull));
        exeCheck("update : email 공백", () -> userService.update(updateEmailEmpty));
        exeCheck("update : email null", () -> userService.update(updateEmailNull));
        exeCheck("update : email 잘못된 형식", () -> userService.update(updateEmailFormDiff));
        exeCheck("update : password 공백", () -> userService.update(updatePasswordEmpty));
        exeCheck("update : password null", () -> userService.update(updatePasswordNull));
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

    static void channelTest(ChannelService channelService
            , MessageService messageService
            , ReadStatusService readStatusService
            , UserService userService) {
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
        createMessage(messageService, publicChannel.getId(), users.get(0).getId(), "안녕하세요");
        createMessage(messageService, publicChannel.getId(), users.get(1).getId(), "안녕하세요");
        createMessage(messageService, privateChannel.getId(), users.get(1).getId(), "안녕하세요");
        createMessage(messageService, privateChannel.getId(), users.get(2).getId(), "안녕하세요");

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

    static void messageTest(MessageService messageService
            , UserService userService
            , ChannelService channelService
            , BinaryContentService binaryContentService) {
        section("Message 테스트");
        User user1 = createUser(userService, "messageUser1", "4321", "message1@aaa.com");
        User user2 = createUser(userService, "messageUser2", "4321", "message2@aaa.com");
        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        Channel channel1 = createPublicChannel(channelService, "public", "public 채널");
        Channel channel2 = createPrivateChannel(channelService, users, null, null);
        BinaryContent binaryContent1 = createBinaryContent(binaryContentService, "images.jpg", "jpg");
        BinaryContent binaryContent2 = createBinaryContent(binaryContentService, "images.jpg", "jpg");
        List<UUID> attachmentIds = new ArrayList<>();
        attachmentIds.add(binaryContent1.getId());
        attachmentIds.add(binaryContent2.getId());

        // 생성
        Optional<Message> messageOpt = exeCheck(
                "create : 정상", () -> createMessage(messageService, channel1.getId(), user1.getId(), attachmentIds, "test1"));
        exeCheck("create : 존재하지 않는 채널", () -> createMessage(messageService, testId, user1.getId(), attachmentIds, "test1"));
        exeCheck("create : 존재하지 않는 유저", () -> createMessage(messageService, channel1.getId(), testId, attachmentIds, "test1"));
        exeCheck("create : 둘 다 없을 때", () -> createMessage(messageService, testId, testId, attachmentIds, "test1"));
        exeCheck("create : channelId null", () -> createMessage(messageService, null, user1.getId(), attachmentIds, "test1"));
        exeCheck("create : authorId null", () -> createMessage(messageService, channel1.getId(), null, attachmentIds, "test1"));
        exeCheck("create : 둘 다 null", () -> createMessage(messageService, null, null, attachmentIds, "test1"));
        createMessage(messageService, channel1.getId(), user1.getId(), "test2");
        createMessage(messageService, channel1.getId(), user1.getId(), "test4");
        createMessage(messageService, channel2.getId(), user2.getId(), "test3");

        Message message = messageOpt.orElseThrow(NoSuchElementException::new);

        // 조회
        exeCheck("findById : 정상", () -> messageService.find(message.getId()));
        exeCheck("findById : 존재하지 않는 메세지", () -> messageService.find(testId));
        System.out.println("findAllByChannelId : ");
        exeCheck("findAllByChannelId : 정상", () -> messageService.findAllByChannelId(channel1.getId()).forEach(System.out::println));
        exeCheck("findAllByChannelId : 채널이 없을 시 empty 리스트", () -> System.out.println(messageService.findAllByChannelId(testId).isEmpty()));

        // 수정
        exeCheck("수정 전 조회", () -> messageService.find(message.getId()));
        MessageUpdateRequest updateRequest = new MessageUpdateRequest(message.getId(), "updatedContent");
        MessageUpdateRequest updateNotFound = new MessageUpdateRequest(testId, "updatedContent");
        exeCheck("update : 정상", () -> messageService.update(updateRequest));
        exeCheck("update : 존재하지 않는 메세지", () -> messageService.update(updateNotFound));
        exeCheck("수정 후 조회", () -> messageService.find(message.getId()));

        // 삭제
        exeCheck("delete : 정상", () -> messageService.delete(message.getId()));
        exeCheck("delete : 삭제된 메세지", () -> messageService.delete(message.getId()));
        exeCheck("delete : 존재하지 않는 메세지", () -> messageService.delete(testId));
        exeCheck("BinaryContent 삭제 확인", () -> binaryContentService.findAllByIdIn(attachmentIds));
    }

    static void readStatusTest(ReadStatusService readStatusService, ChannelService channelService, User user) {
        section("readStatus 테스트");
        List<User> users = new ArrayList<>();
        users.add(user);
        // 생성
        Channel channel1 = createPrivateChannel(channelService, users, "private1", "description");
        exeCheck("create : 이미 존재하는 UserStatus", () -> createReadStatus(readStatusService, user.getId(), channel1.getId()));
        exeCheck("create : 존재하지 않는 유저", () -> createReadStatus(readStatusService, testId, channel1.getId()));
        exeCheck("create : 존재하지 않는 채널", () -> createReadStatus(readStatusService, user.getId(), testId));
        exeCheck("create : 둘 다 존재하지 않음", () -> createReadStatus(readStatusService, testId, testId));

        // 조회
        ReadStatus readStatus = readStatusService.findAllByUserId(users.get(0).getId()).get(0);
        exeCheck("findById : 정상", () -> readStatusService.findById(readStatus.getId()));
        exeCheck("findById : 존재하지 않는 UserStatus", () -> readStatusService.findById(testId));
        exeCheck("findAllByUserId : 정상", () -> readStatusService.findAllByUserId(user.getId()).forEach(System.out::println));
        exeCheck("findAllByUserId : 존재하지 않는 유저, 정상 작동 시 empty", () -> readStatusService.findAllByUserId(testId));

        // 수정
        ReadStatusUpdateRequest updateRequest = new ReadStatusUpdateRequest(readStatus.getId(), true);
        ReadStatusUpdateRequest updateNotFound = new ReadStatusUpdateRequest(testId, true);
        exeCheck("수정 전 조회", () -> readStatusService.findById(readStatus.getId()));
        exeCheck("update : 정상", () -> readStatusService.update(updateRequest));
        exeCheck("update : 존재하지 않는 UserStatus", () -> readStatusService.update(updateNotFound));
        exeCheck("수정 후 조회", () -> readStatusService.findById(readStatus.getId()));

        // 삭제
        exeCheck("delete : 정상", () -> readStatusService.delete(readStatus.getId()));
        exeCheck("delete : 삭제된 UserStatus", () -> readStatusService.delete(readStatus.getId()));
        exeCheck("delete : 존재하지 않는 UserStatus", () -> readStatusService.delete(testId));
    }

    static void authTest(AuthService authService, User user) {
        section("ReadStatus 테스트");

        UserLoginRequest request = new UserLoginRequest(user.getUsername(), user.getPassword());
        UserLoginRequest requestWrongUsername = new UserLoginRequest(testString, user.getPassword());
        UserLoginRequest requestWrongPassword = new UserLoginRequest(user.getUsername(), testString);
        UserLoginRequest requestWrongBoth = new UserLoginRequest(testString, testString);

        exeCheck("login : 정상", () -> System.out.println(authService.login(request)));
        exeCheck("login : username 틀림", () -> System.out.println(authService.login(requestWrongUsername)));
        exeCheck("login : password 틀림", () -> System.out.println(authService.login(requestWrongPassword)));
        exeCheck("login : 둘 다 틀림", () -> System.out.println(authService.login(requestWrongBoth)));
    }

    static ReadStatus createReadStatus(ReadStatusService readStatusService, UUID userId, UUID channelId) {
        ReadStatusCreateRequest readStatusCreateRequest = new ReadStatusCreateRequest(userId, channelId);
        return readStatusService.create(readStatusCreateRequest);
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
        Message message = createMessage(messageService, channel.getId(), user.getId(), new ArrayList<>(), "안녕하세요.");

//        binaryContentTest(binaryContentService, message);
//        userTest(userService, userStatusService, binaryContentService);
//        userStatusTest(userStatusService, user);
//        channelTest(channelService, messageService, readStatusService, userService);
        messageTest(messageService, userService, channelService, binaryContentService);
//        readStatusTest(readStatusService, channelService, user);
//        authTest(authService, user);

        userService.deleteAll();
        channelService.deleteAll();
        messageService.deleteAll();
        binaryContentService.deleteAll();
        readStatusService.deleteAll();
        userStatusService.deleteAll();
    }
}