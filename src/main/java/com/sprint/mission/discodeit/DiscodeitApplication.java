package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.auth.LoginResponseDto;
import com.sprint.mission.discodeit.dto.binarycontent.FileResponseDto;
import com.sprint.mission.discodeit.dto.binarycontent.FileUploadDto;
import com.sprint.mission.discodeit.dto.channel.ChannelCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageViewDto;
import com.sprint.mission.discodeit.dto.readstatus.ChannelUnreadStatusDto;
import com.sprint.mission.discodeit.dto.user.UserRegisterDto;
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

        testUser = userService.create(new UserRegisterDto("테스트유저", "test@example.com", "1234", null));
        testChannel = channelService.create(new ChannelCreateDto("테스트채널", ChannelType.PUBLIC, null, null));

        System.out.println("\n 🚀 유저 테스트 🚀 \n");
        testUser();
        System.out.println("\n ❌ 유저 테스트 - 예외처리 ❌ \n");
        testUserFail();

        System.out.println("\n 🚀 채널 테스트 🚀 \n");
        testChannel();
        System.out.println("\n ❌ 채널 테스트 - 예외처리 ❌ \n");
        testChannelFail();

        System.out.println("\n 🚀 메시지 테스트 🚀 \n");
        testMessage();
        System.out.println("\n ❌ 메시지 테스트 - 예외처리 ❌ \n");
        testMessageFail();

        System.out.println("\n 🚀 메시지상태 테스트 🚀 \n");
        testReadStatus();
        System.out.println("\n ❌ 메시지상태 테스트 - 예외처리 ❌ \n");
        testReadStatusFail();

        System.out.println("\n 🚀 유저 상태 테스트 🚀 \n");
        testUserStatus();
        System.out.println("\n ❌ 유저 상태 예외처리 테스트 ❌ \n");
        testUserStatusFail();

    }

    private void testUser() {

        // 1. Create DTO 생성
        var dto = new UserRegisterDto("소연3", "soyeon3@gmail.com", "1234", null);

        // 2. 사용자 생성
        User newUser = userService.create(dto);
        System.out.println(newUser);

        // 로그인
        LoginResponseDto user = authService.login("soyeon3@gmail.com", "1234");
        System.out.println(user);

        // 전체 유저 조회
        List<User> userList = userService.findAll();
        System.out.println(userList);

        // 이메일로 조회
        User byEmail = userService.findByEmail(user.email());

        System.out.println("--- 이메일 조회 결과 ---");
        System.out.println(byEmail);

        // ID로 조회
        User byId = userService.findById(user.id());

        System.out.println("--- ID 조회 결과 ---");
        System.out.println(byId);
    }

    private void testUserFail() {

        // GIVEN: 이미 존재하는 이메일로 가입 시도
        var test1 = new UserRegisterDto("소연3", "soyeon3@gmail.com", "1234", null);

        try {
            userService.create(test1);
            System.err.println("중복 이메일 예외가 발생하지 않았습니다.");
        } catch (IllegalArgumentException e) {
            System.out.println("중복 이메일 예외 발생: " + e.getMessage());
        }

        // GIVEN: 존재하지 않는 이메일로 로그인
        try {
            authService.login("notfound@gmail.com", "1234");
            System.err.println("존재하지 않는 이메일 예외가 발생하지 않았습니다.");
        } catch (IllegalArgumentException e) {
            System.out.println("존재하지 않는 이메일 예외 발생: " + e.getMessage());
        }

        // GIVEN: 비밀번호 불일치
        try {
            authService.login("soyeon3@gmail.com", "wrong-password");
            System.err.println("비밀번호 불일치 예외가 발생하지 않았습니다.");
        } catch (IllegalArgumentException e) {
            System.out.println("비밀번호 불일치 예외 발생: " + e.getMessage());
        }

        // GIVEN: 존재하지 않는 ID로 조회
        try {
            UUID fakeId = UUID.randomUUID();
            userService.findById(fakeId);
            System.err.println("존재하지 않는 ID 예외가 발생하지 않았습니다.");
        } catch (IllegalArgumentException e) {
            System.out.println("존재하지 않는 ID 예외 발생: " + e.getMessage());
        }
    }

    private void testChannel() {

        var dto = new ChannelCreateDto("공부채널", ChannelType.PUBLIC, null, null);
        Channel testChannel = channelService.create(dto);
        System.out.println("1) 채널 생성됨: " + testChannel);

        List<Channel> channelList = channelService.findAll();
        System.out.println("2) 모든 채널 목록: \n" + channelList);

        sleep(3000);
        channelService.updateTopic(testChannel.getId(), "공부채널");
        System.out.println("3) 토픽 업데이트: \n" + testChannel);

        sleep(3000);
        channelService.updateDescription(testChannel.getId(), "공부채널 입니다.");
        System.out.println("4) 설명 업데이트: \n" + testChannel);

        // WHEN: ID로 조회
        Channel foundById = channelService.findById(testChannel.getId());
        System.out.println("5) ID로 조회된 채널: \n" + foundById);

        // WHEN: 이름으로 조회
        List<Channel> foundByName = channelService.findByName("공부채널");
        System.out.println("6) 이름으로 조회된 채널: \n" + foundByName);

        // WHEN: 채널 삭제
        boolean deleted = channelService.delete(testChannel.getId());
        System.out.println("7) 삭제 결과: " + (deleted ? "성공" : "실패"));
    }

    private void testChannelFail() {

        // 채널 이름 없음
        var dto = new ChannelCreateDto(null, ChannelType.PRIVATE, null, null);
        try{
            channelService.create(dto);
            System.err.println("❌ 이름이 null인데 예외가 발생하지 않음!!");
        }catch (IllegalArgumentException e){
            System.out.println("채널 생성 예외 발생: " + e.getMessage());
        }


        // WHEN: 존재하지 않는 채널 ID로 조회
        UUID invalidId = UUID.randomUUID();
        try {
            channelService.findById(invalidId);
            System.err.println("❌ 존재하지 않는 ID인데 예외가 발생하지 않음!!");
        } catch (IllegalArgumentException e) {
            System.out.println("아이디로 조회 예외 발생: " + e.getMessage());
        }

        // WHEN: 존재하지 않는 채널에 토픽 업데이트
        try {
            channelService.updateTopic(invalidId, "업데이트된 토픽");
            System.err.println("❌ 없는 채널인데 토픽 업데이트 성공?");
        } catch (IllegalArgumentException e) {
            System.out.println("토픽 업데이트 예외 발생: " + e.getMessage());
        }

        // WHEN: 존재하지 않는 채널에 설명 업데이트
        try {
            channelService.updateDescription(invalidId, "설명 업데이트");
            System.err.println("❌ 없는 채널인데 설명 업데이트 성공?");
        } catch (IllegalArgumentException e) {
            System.out.println("설명 업데이트 예외 발생: " + e.getMessage());
        }

        // WHEN: 존재하지 않는 채널 삭제
        boolean result = channelService.delete(invalidId);
        if (!result) {
            System.out.println("존재하지 않는 채널 삭제 실패 (정상 동작)");
        } else {
            System.err.println("❌ 존재하지 않는 채널 삭제 성공?");
        }
    }

    private void testMessage() {
        // 1) 메시지 생성
        var messageDto = new MessageCreateDto(testUser.getId(), testChannel.getId(), "안녕하세요", null);
        Message testMessage = messageService.create(messageDto);

        MessageViewDto message1 = toMessageView(testMessage);
        System.out.println("1) 생성된 메시지 \n" + message1);

        // 2) 메시지 수정
        Message testMessage2 = messageService.update(testMessage.getId(), "반갑습니다!");
        MessageViewDto message2 = toMessageView(testMessage2);
        System.out.println("2) 수정된 메시지: \n" + message2);

        // 3) 내용 검색된 메시지 리스트 출력
        List<MessageViewDto> messageList = messageService.findByContent("반갑").stream()
                .map(this::toMessageView)
                .toList();
        System.out.println("3) 모든 메시지 리스트 \n" + messageList);

        // 4. findByUserId
        List<MessageViewDto> userMessages = messageService.findByUserId(testUser.getId()).stream()
                .map(this::toMessageView)
                .toList();
        System.out.println("4) 해당 유저의 메시지들\n" + userMessages);

        // 5. 파일 첨부
        FileUploadDto testFile = new FileUploadDto("test.txt", "text/plain", "테스트".getBytes(), 9);
        BinaryContent file = binaryContentService.save(testFile);

        messageService.attachFile(testMessage.getId(), file.getId());
        MessageViewDto withFile = toMessageView(messageService.findById(testMessage.getId()));
        System.out.println("5) 파일 첨부된 메시지\n" + withFile);

        // 6. 파일 제거
        messageService.detachFile(testMessage.getId(), file.getId());
        MessageViewDto noFile = toMessageView(messageService.findById(testMessage.getId()));
        System.out.println("6) 파일 제거된 메시지\n" + noFile);

        // 7. findByContent
        List<MessageViewDto> matched = messageService.findByContent("반갑").stream()
                .map(this::toMessageView)
                .toList();
        System.out.println("7) '반갑' 포함된 메시지\n" + matched);

        // 8. 전체 메시지 조회
        List<MessageViewDto> all = messageService.findAll().stream()
                .map(this::toMessageView)
                .toList();
        System.out.println("8) 전체 메시지\n" + all);

        // 9. 메시지 삭제
        boolean deleted = messageService.delete(testMessage.getId());
        System.out.println("9) 메시지 삭제됨? " + deleted);
    }

    private void testMessageFail() {
        UUID invalidId = UUID.randomUUID(); // 존재하지 않는 메시지 ID
        UUID randomFileId = UUID.randomUUID(); // 존재하지 않는 파일 ID

        // 1. 존재하지 않는 메시지 조회
        try {
            messageService.findById(invalidId);
            System.err.println("❌ 존재하지 않는 메시지를 조회했는데 예외가 발생하지 않음!");
        } catch (IllegalArgumentException e) {
            System.out.println("1) 메시지 조회 예외 발생: " + e.getMessage());
        }

        // 2. 존재하지 않는 메시지 수정
        try {
            messageService.update(invalidId, "수정할 수 없습니다.");
            System.err.println("❌ 존재하지 않는 메시지를 수정했는데 예외가 발생하지 않음!");
        } catch (IllegalArgumentException e) {
            System.out.println("2) 메시지 수정 예외 발생: " + e.getMessage());
        }

        // 3. 존재하지 않는 메시지에 파일 첨부
        try {
            messageService.attachFile(invalidId, randomFileId);
            System.err.println("❌ 존재하지 않는 메시지에 파일을 첨부했는데 예외가 발생하지 않음!");
        } catch (IllegalArgumentException e) {
            System.out.println("3) 파일 첨부 예외 발생: " + e.getMessage());
        }

        // 4. 존재하지 않는 메시지에서 파일 제거
        try {
            messageService.detachFile(invalidId, randomFileId);
            System.err.println("❌ 존재하지 않는 메시지에서 파일 제거했는데 예외가 발생하지 않음!");
        } catch (IllegalArgumentException e) {
            System.out.println("4) 파일 제거 예외 발생: " + e.getMessage());
        }

        // 5. 존재하지 않는 메시지 삭제
        boolean deleted = messageService.delete(invalidId);
        if (!deleted) {
            System.out.println("5) 존재하지 않는 메시지 삭제 실패 (정상 동작)");
        } else {
            System.err.println("❌ 존재하지 않는 메시지 삭제 성공?");
        }
    }

    private void testReadStatus() {
        // GIVEN: 테스트용 사용자와 채널, 메시지 생성
        User user = userService.create(new UserRegisterDto("테스터", "test@email.com", "1234", null));
        Channel channel = channelService.create(new ChannelCreateDto("리드채널", ChannelType.PUBLIC, null, null));

        // 메시지 2개 생성
        Message message1 = messageService.create(new MessageCreateDto(user.getId(), channel.getId(), "첫번째 메시지", null));
        sleep(1000); // 시간 차이 주기
        Message message2 = messageService.create(new MessageCreateDto(user.getId(), channel.getId(), "두번째 메시지", null));

        // WHEN: 아직 읽은 상태 없음 → 전체 메시지 2개 unread
        int unread1 = readStatusService.countUnreadMessages(user.getId(), channel.getId());
        System.out.println("1) 읽지 않은 메시지 수 (초기): " + unread1); // → 2

        // 메시지 1개만 읽음 처리
        readStatusService.updateLastReadAt(user.getId(), channel.getId(), message1.getCreatedAt());

        // WHEN: 메시지1까지만 읽음 → 메시지2는 unread
        int unread2 = readStatusService.countUnreadMessages(user.getId(), channel.getId());
        System.out.println("2) 읽지 않은 메시지 수 (message1까지 읽음): " + unread2); // → 1

        // 모든 메시지 읽음 처리
        readStatusService.updateLastReadAt(user.getId(), channel.getId(), message2.getCreatedAt());

        // WHEN: 모두 읽음 처리
        int unread3 = readStatusService.countUnreadMessages(user.getId(), channel.getId());
        System.out.println("3) 읽지 않은 메시지 수 (모두 읽음): " + unread3); // → 0

        // AND: getUnreadChannels 테스트 (false가 되어야 함)
        List<ChannelUnreadStatusDto> unreadChannels = readStatusService.getUnreadChannels(user.getId());
        System.out.println("4) 안읽은 채널 정보: " + unreadChannels); // hasUnread=false

    }

    private void testReadStatusFail() {
        UUID fakeUserId = UUID.randomUUID();
        UUID fakeChannelId = UUID.randomUUID();

        // WHEN: 존재하지 않는 사용자, 채널로 읽음 시간 업데이트 시도
        try {
            readStatusService.updateLastReadAt(fakeUserId, fakeChannelId, Instant.now());
            System.err.println("존재하지 않는 유저/채널인데 읽음 시간 업데이트 성공?");
        } catch (IllegalArgumentException e) {
            System.out.println("예외 발생 (읽음 시간 업데이트 실패): " + e.getMessage());
        }

        // WHEN: 존재하지 않는 사용자/채널 조합으로 ReadStatus 조회
        try {
            readStatusService.findByUserIdAndChannelId(fakeUserId, fakeChannelId);
            System.err.println("존재하지 않는 ReadStatus인데 조회 성공?");
        } catch (IllegalArgumentException e) {
            System.out.println("예외 발생 (ReadStatus 조회 실패): " + e.getMessage());
        }

        // WHEN: 존재하지 않는 채널의 읽지 않은 메시지 수 조회
        try {
            int count = readStatusService.countUnreadMessages(fakeUserId, fakeChannelId);
            System.out.println("존재하지 않는 채널인데 메시지 수 조회 성공? → " + count);
        } catch (IllegalArgumentException e) {
            System.out.println("예외 발생 (countUnreadMessages 실패): " + e.getMessage());
        }

        // WHEN: 해당 유저의 읽지 않은 채널 리스트 조회 (비정상 상황은 아니지만, 비어있는 경우 확인)
        List<ChannelUnreadStatusDto> result = readStatusService.getUnreadChannels(fakeUserId);
        if (result.isEmpty()) {
            System.out.println("읽지 않은 채널 없음 (정상)");
        } else {
            System.err.println("읽지 않은 채널이 존재? → " + result);
        }
    }

    private void testUserStatus() {
        // GIVEN
        UUID userId = testUser.getId();

        // 1) 온라인 여부 확인
        boolean online = userStatusService.isOnline(userId);
        System.out.println("1) 현재 온라인 상태: " + online);

        // 2) 마지막 접속 시간 업데이트
        userStatusService.updateLastAccessedAt(testUser);
        System.out.println("2) 마지막 접속 시간 업데이트 완료");

        // 3) 다시 온라인 여부 확인 (변화는 없지만 로직 실행 검증)
        boolean onlineAfter = userStatusService.isOnline(userId);
        System.out.println("3) 업데이트 후 온라인 상태: " + onlineAfter);
    }

    private void testUserStatusFail() {
        UUID fakeUserId = UUID.randomUUID();

        // 1) 존재하지 않는 유저의 온라인 상태 확인 시도
        try {
            userStatusService.isOnline(fakeUserId);
            System.err.println("❌ 존재하지 않는 유저인데 isOnline 성공?");
        } catch (IllegalArgumentException e) {
            System.out.println("테스트 성공 🙆🏻‍♀️ 예외 발생 (isOnline 실패): " + e.getMessage());
        }

        // 2) null 유저로 마지막 접속 시간 업데이트 시도
        try {
            userStatusService.updateLastAccessedAt(null);
            System.err.println("❌ null 유저로 updateLastAccessedAt 성공?");
        } catch (NullPointerException | IllegalArgumentException e) {
            System.out.println("테스트 성공 🙆🏻‍♀️ 예외 발생 (updateLastAccessedAt 실패): " + e.getMessage());
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private MessageViewDto toMessageView(Message message) {
        List<FileResponseDto> files = message.getFiles().stream()
                .map(id -> binaryContentService.findById(id)
                        .map(file -> new FileResponseDto(
                                file.getId(),
                                file.getFileName(),
                                file.getContentType(),
                                file.getContent().length,
                                "/api/files/" + file.getId()
                        )).orElseThrow(() -> new IllegalArgumentException("파일이 존재하지 않음: " + id))
                ).toList();

        return new MessageViewDto(
                message.getId(),
                testUser.getId(),
                testUser.getName(),
                testChannel.getId(),
                testChannel.getName(),
                message.getContent(),
                files
        );
    }
}
