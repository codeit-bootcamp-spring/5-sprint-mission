package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Instant;
import java.util.List;
import java.util.UUID;


@SpringBootApplication
public class DiscodeitApplication implements CommandLineRunner {

    private final UserService userService;
    private final AuthService authService;
    private final UserStatusService userStatusService;
    private final ChannelService channelService;
    private final MessageService messageService;
    private final ReadStatusService readStatusService;
    private final BinaryContentService binaryContentService;

    private User testUser;
    private Channel testChannel;

    public DiscodeitApplication(
            UserService userService,
            AuthService authService,
            UserStatusService userStatusService,
            ChannelService channelService,
            MessageService messageService,
            ReadStatusService readStatusService,
            BinaryContentService binaryContentService
    ) {
        this.userService = userService;
        this.authService = authService;
        this.userStatusService = userStatusService;
        this.channelService = channelService;
        this.messageService = messageService;
        this.readStatusService = readStatusService;
        this.binaryContentService = binaryContentService;
    }

    public static void main(String[] args) {
        SpringApplication.run(DiscodeitApplication.class, args);
    }

    @Override
    public void run(String... args) {

        testUser = userService.create(new UserDto.Create("테스트유저", "test@example.com", "1234", null));
        testChannel = channelService.create(new ChannelDto.Create("테스트채널", ChannelType.TEXT, null, null));

        System.out.println("\n 🚀 유저 테스트 🚀 \n");
        testUser();

        System.out.println("\n 🚀 채널 테스트 🚀 \n");
        testChannel();

        System.out.println("\n 🚀 메시지 테스트 🚀 \n");
        testMessage();

        System.out.println("\n 🚀 메시지 상태 테스트 🚀 \n");
        testReadStatus();

    }

    private void testUser() {

        // 1. Create DTO 생성
        var dto = new UserDto.Create("소연3", "soyeon3@gmail.com", "1234", null);

        // 2. 사용자 생성
        User user = userService.create(dto);
        System.out.println(user);

        // 로그인
        AuthDto.View loginUser = authService.login("soyeon3@gmail.com", "1234");
        System.out.println(loginUser);

        // 전체 유저 조회
        List<User> userList = userService.findAll();
        System.out.println(userList);

        // 이메일로 조회
        User byEmail = userService.findByEmail(loginUser.email());

        System.out.println("--- 이메일 조회 결과 ---");
        System.out.println(byEmail);

        // ID로 조회
        User byId = userService.findById(loginUser.id());

        System.out.println("--- ID 조회 결과 ---");
        System.out.println(byId);

        System.out.println("🎉 테스트 완료");
    }

    private void testChannel() {


        // 1. 모든 채널 조회
        System.out.println("----- 채널 목록 ----");
        List<Channel> channelList = channelService.findAll();
        System.out.println(channelList);

        // 1. Dto 생성
        var dto = new ChannelDto.Create("공부채널", ChannelType.TEXT, null, null);

        // 2. 채널 생성
        Channel created = channelService.create(dto);
        UUID id = created.getId(); // 채널 아이디
        System.out.println("---생성된 채널---");
        System.out.println(created);

        // 3. 채널 토픽 업데이트
        sleep(3000);
        channelService.updateTopic(id, "공부채널");
        System.out.println("채널 토픽 업데이트: " + created);

        // 4. 채널 설명 업데이트
        sleep(3000);
        channelService.updateDescription(id, "공부채널 입니다.");
        System.out.println("채널 설명 업데이트: " + created);

        // 아이디로 채널 조회
        Channel foundById = channelService.findById(id);
        System.out.println("--- ID로 조회한 채널 ---");
        System.out.println(foundById);

        // 이름으로 채널 조회
        List<Channel> foundByName = channelService.findByName("공부채널");
        System.out.println("--- 이름으로 조회한 채널 ---");
        System.out.println(foundByName);

        // 8. 채널 삭제
        boolean result = channelService.delete(id);
        if (result) {
            System.out.println("삭제완료");
        }
    }

    private void testMessage() {


        var userDto = new UserDto.Create("소연", "soyeon@gmail.com", "1234", null);
        User user = userService.create(userDto);

        var channelDto = new ChannelDto.Create("공부채널", ChannelType.TEXT, null, null);
        Channel channel = channelService.create(channelDto);

        // 메시지 생성
        var messageDto = new MessageDto.Create(user.getId(), channel.getId(), "안녕하세요", null);
        Message message = messageService.create(messageDto);

        System.out.println(message);

        messageService.update(message.getId(), "반갑습니다!");
        System.out.println("수정된 메시지: " + message);

        List<Message> messageList = messageService.findByContent("반갑");
        System.out.println(messageList);
    }

    private void testReadStatus() {


        // 사용자, 채널 생성
        var userDto = new UserDto.Create("소연2", "soyeon2@email.com", "1234", null);
        User user = userService.create(userDto);

        var channelDto = new ChannelDto.Create("기능 테스트 채널", ChannelType.TEXT, null, null);
        Channel channel = channelService.create(channelDto);

        // 메시지 3개 생성 (시간차를 주기 위해 약간 지연)
        Message msg1 = messageService.create(new MessageDto.Create(user.getId(), channel.getId(), "메시지1", null));
        sleep(2000); // 2초 정도 시간차 주기
        Message msg2 = messageService.create(new MessageDto.Create(user.getId(), channel.getId(), "메시지2", null));
        sleep(2000);
        Message msg3 = messageService.create(new MessageDto.Create(user.getId(), channel.getId(), "메시지3", null));

        System.out.println("메시지 3개 생성 완료");

        System.out.println("전체 메시지 목록");
        List<Message> messageList = messageService.findAll();
        System.out.println(messageList);

        // 특정 시점으로 읽음 상태 등록
        Instant readAt = msg2.getCreatedAt(); // 두 번째 메시지까지 읽었다고 가정
        readStatusService.updateLastReadAt(user.getId(), channel.getId(), readAt);

        // 아직 ReadStatus 없음 → getUnreadChannels 호출 전 update 없이 호출해도 동작
        List<ReadStatusDto.ChannelUnreadStatus> unreadList = readStatusService.getUnreadChannels(user.getId());
        System.out.println("| 아직 읽지 않은 채널: " + unreadList);

        // countUnreadMessages 확인 → 마지막 메시지만 읽지 않은 것으로 판단
        int unreadCount = readStatusService.countUnreadMessages(user.getId(), channel.getId());
        System.out.println("| 안 읽은 메시지 수: " + unreadCount); // 기대: 1

        // findByUserIdAndChannelId 검증
        ReadStatus rs = readStatusService.findByUserIdAndChannelId(user.getId(), channel.getId());
        System.out.println("| 현재 읽은 시간: " + rs.getLastReadAt());

        System.out.println("[ReadStatusService 기능 테스트 완료]");

    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
