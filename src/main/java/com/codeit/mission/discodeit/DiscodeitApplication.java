package com.codeit.mission.discodeit;

import com.codeit.mission.discodeit.dto.auth.LoginRequest;
import com.codeit.mission.discodeit.dto.auth.LoginResponse;
import com.codeit.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.codeit.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.codeit.mission.discodeit.dto.channel.ChannelResponse;
import com.codeit.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.codeit.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.codeit.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.codeit.mission.discodeit.dto.message.AttachmentRequest;
import com.codeit.mission.discodeit.dto.message.MessageCreateRequest;
import com.codeit.mission.discodeit.dto.message.MessageResponse;
import com.codeit.mission.discodeit.dto.message.MessageUpdateRequest;
import com.codeit.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.codeit.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.codeit.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.codeit.mission.discodeit.dto.user.ProfileImageRequest;
import com.codeit.mission.discodeit.dto.user.UserCreateRequest;
import com.codeit.mission.discodeit.dto.user.UserResponse;
import com.codeit.mission.discodeit.dto.user.UserUpdateRequest;
import com.codeit.mission.discodeit.dto.userstatus.UserStatusCreateRequest;
import com.codeit.mission.discodeit.dto.userstatus.UserStatusResponse;
import com.codeit.mission.discodeit.dto.userstatus.UserStatusUpdateByUserRequest;
import com.codeit.mission.discodeit.entity.*;
import com.codeit.mission.discodeit.repository.*;
import com.codeit.mission.discodeit.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@SpringBootApplication
@EnableConfigurationProperties
public class DiscodeitApplication implements CommandLineRunner {

    // Repository 의존성
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;

    // Service 의존성
    private final UserService userService;
    private final AuthService authService;
    private final ChannelService channelService;
    private final MessageService messageService;
    private final ReadStatusService readStatusService;
    private final UserStatusService userStatusService;
    private final BinaryContentService binaryContentService;

    public DiscodeitApplication(@Qualifier("userRepository") UserRepository userRepository,
                                @Qualifier("userStatusRepository") UserStatusRepository userStatusRepository,
                                @Qualifier("binaryContentRepository") BinaryContentRepository binaryContentRepository,
                                @Qualifier("channelRepository") ChannelRepository channelRepository,
                                @Qualifier("messageRepository") MessageRepository messageRepository,
                                @Qualifier("readStatusRepository") ReadStatusRepository readStatusRepository,
                                UserService userService,
                                AuthService authService,
                                ChannelService channelService,
                                MessageService messageService,
                                ReadStatusService readStatusService,
                                UserStatusService userStatusService,
                                BinaryContentService binaryContentService) {
        this.userRepository = userRepository;
        this.userStatusRepository = userStatusRepository;
        this.binaryContentRepository = binaryContentRepository;
        this.channelRepository = channelRepository;
        this.messageRepository = messageRepository;
        this.readStatusRepository = readStatusRepository;
        this.userService = userService;
        this.authService = authService;
        this.channelService = channelService;
        this.messageService = messageService;
        this.readStatusService = readStatusService;
        this.userStatusService = userStatusService;
        this.binaryContentService = binaryContentService;
    }

    public static void main(String[] args) {
        SpringApplication.run(DiscodeitApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("=== DiscodeitApplication 시작 ===");
        log.info("Repository 타입 확인: {}", userRepository.getClass().getSimpleName());

        try {
            testAllRepositories();
            log.info("");
            testAllServices();

        } catch (Exception e) {
            log.error("테스트 실행 중 오류 발생", e);
        }

        log.info("=== DiscodeitApplication 완료 ===");
    }

    private void testAllRepositories() {
        log.info("=== 전체 Repository 테스트 시작 ===");

        testUserRepository();
        log.info("");

        testUserStatusRepository();
        log.info("");

        testBinaryContentRepository();
        log.info("");

        testChannelRepository();
        log.info("");

        testMessageRepository();
        log.info("");

        testReadStatusRepository();
    }

    private void testAllServices() {
        log.info("=== 전체 Service 테스트 시작 ===");

        testBinaryContentService();
        log.info("");

        testUserService();
        log.info("");

        testAuthService();
        log.info("");

        testUserStatusService();
        log.info("");

        testChannelService();
        log.info("");

        testMessageService();
        log.info("");

        testReadStatusService();
    }

    private void testUserRepository() {
        log.info("=== User Repository 테스트 ===");

        log.info("=== User 등록 ===");
        User user1 = new User("홍길동", "test1@email.com", "1234");
        User user2 = new User("김길동", "test2@email.com", "4321");

        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);

        log.info("생성된 사용자 1: {}", savedUser1);
        log.info("생성된 사용자 2: {}", savedUser2);

        log.info("=== User 단건 조회 ===");
        Optional<User> foundUser = userRepository.findById(savedUser1.getId());
        log.info("조회된 사용자: {}", foundUser.orElse(null));
        log.info("user1과 조회된 User가 동일한가? : {}", savedUser1.equals(foundUser.orElse(null)));

        log.info("=== User 다건 조회 ===");
        List<User> allUsers = userRepository.findAll();
        log.info("user 개수: {}", allUsers.size());
        log.info("전체 사용자: {}", allUsers);

        log.info("=== User 수정 ===");
        savedUser2.update("김김길동", "1234@email.com", "123456");
        User updatedUser = userRepository.save(savedUser2);
        log.info("수정된 사용자: {}", updatedUser);

        log.info("=== User 삭제 ===");
        userRepository.deleteById(savedUser1.getId());
        userRepository.deleteById(savedUser2.getId());

        log.info("=== User 삭제 확인 ===");
        List<User> remainingUsers = userRepository.findAll();
        log.info("삭제 후 사용자 목록: {}", remainingUsers);

        log.info("=== User Repository 테스트 완료 ===");
    }

    private void testUserStatusRepository() {
        log.info("=== UserStatus Repository 테스트 ===");

        User testUser = new User("test_user", "test@example.com", "password");
        User savedUser = userRepository.save(testUser);

        log.info("=== UserStatus 등록 ===");
        UserStatus status1 = new UserStatus(savedUser.getId(), Instant.now());
        UserStatus status2 = new UserStatus(savedUser.getId(), Instant.now().plusSeconds(60));

        UserStatus savedStatus1 = userStatusRepository.save(status1);
        UserStatus savedStatus2 = userStatusRepository.save(status2);

        log.info("생성된 UserStatus 1: {}", savedStatus1);
        log.info("생성된 UserStatus 2: {}", savedStatus2);

        log.info("=== UserStatus 단건 조회 ===");
        Optional<UserStatus> foundStatus = userStatusRepository.findById(savedStatus1.getId());
        log.info("조회된 UserStatus: {}", foundStatus.orElse(null));
        log.info("status1과 조회된 UserStatus가 동일한가? : {}", savedStatus1.equals(foundStatus.orElse(null)));

        log.info("=== UserStatus 다건 조회 ===");
        List<UserStatus> allStatuses = userStatusRepository.findAll();
        log.info("UserStatus 개수: {}", allStatuses.size());
        log.info("전체 UserStatus: {}", allStatuses);

        log.info("=== UserStatus 삭제 ===");
        userStatusRepository.deleteById(savedStatus1.getId());
        userStatusRepository.deleteById(savedStatus2.getId());

        log.info("=== UserStatus 삭제 확인 ===");
        List<UserStatus> remainingStatuses = userStatusRepository.findAll();
        log.info("삭제 후 UserStatus 목록: {}", remainingStatuses);

        userRepository.deleteById(savedUser.getId());

        log.info("=== UserStatus Repository 테스트 완료 ===");
    }

    private void testBinaryContentRepository() {
        log.info("=== BinaryContent Repository 테스트 ===");

        User testUser = new User("test_user", "test@example.com", "password");
        User savedUser = userRepository.save(testUser);

        log.info("=== BinaryContent 등록 ===");
        BinaryContent profileImage = new BinaryContent(
                "profile.jpg",
                "image/jpeg",
                2048L,
                createDummyImageData("profile.jpg"),
                savedUser.getId(),
                null
        );

        BinaryContent attachment = new BinaryContent(
                "document.pdf",
                "application/pdf",
                4096L,
                createDummyFileData("document.pdf"),
                null,
                null
        );

        BinaryContent savedProfile = binaryContentRepository.save(profileImage);
        BinaryContent savedAttachment = binaryContentRepository.save(attachment);

        log.info("생성된 프로필 이미지: {}", savedProfile);
        log.info("생성된 첨부파일: {}", savedAttachment);

        log.info("=== BinaryContent 단건 조회 ===");
        Optional<BinaryContent> foundContent = binaryContentRepository.findById(savedProfile.getId());
        log.info("조회된 BinaryContent: {}", foundContent.orElse(null));
        log.info("profile과 조회된 BinaryContent가 동일한가? : {}", savedProfile.equals(foundContent.orElse(null)));

        log.info("=== BinaryContent 다건 조회 ===");
        List<BinaryContent> allContents = binaryContentRepository.findAll();
        log.info("BinaryContent 개수: {}", allContents.size());
        log.info("전체 BinaryContent: {}", allContents);

        log.info("=== BinaryContent 삭제 ===");
        binaryContentRepository.deleteById(savedProfile.getId());
        binaryContentRepository.deleteById(savedAttachment.getId());

        log.info("=== BinaryContent 삭제 확인 ===");
        List<BinaryContent> remainingContents = binaryContentRepository.findAll();
        log.info("삭제 후 BinaryContent 목록: {}", remainingContents);

        userRepository.deleteById(savedUser.getId());

        log.info("=== BinaryContent Repository 테스트 완료 ===");
    }

    private void testChannelRepository() {
        log.info("=== Channel Repository 테스트 ===");

        log.info("=== Channel 등록 ===");
        Channel channel1 = new Channel(ChannelType.PUBLIC, "스프링 백엔드 5기", "5기");
        Channel channel2 = new Channel(ChannelType.PRIVATE, "스프링 백엔드 6기", "6기");

        Channel savedChannel1 = channelRepository.save(channel1);
        Channel savedChannel2 = channelRepository.save(channel2);

        log.info("생성된 채널 1: {}", savedChannel1);
        log.info("생성된 채널 2: {}", savedChannel2);

        log.info("=== Channel 단건 조회 ===");
        Optional<Channel> foundChannel = channelRepository.findById(savedChannel1.getId());
        log.info("조회된 채널: {}", foundChannel.orElse(null));
        log.info("channel1과 조회된 Channel이 동일한가? : {}", savedChannel1.equals(foundChannel.orElse(null)));

        log.info("=== Channel 다건 조회 ===");
        List<Channel> allChannels = channelRepository.findAll();
        log.info("channel 개수: {}", allChannels.size());
        log.info("전체 채널: {}", allChannels);

        log.info("=== Channel 수정 ===");
        savedChannel1.update("코드잇 스프링 백엔드 5기", null);
        Channel updatedChannel = channelRepository.save(savedChannel1);
        log.info("수정된 채널: {}", updatedChannel);

        log.info("=== Channel 삭제 ===");
        channelRepository.deleteById(savedChannel1.getId());
        channelRepository.deleteById(savedChannel2.getId());

        log.info("=== Channel 삭제 확인 ===");
        List<Channel> remainingChannels = channelRepository.findAll();
        log.info("삭제 후 채널 목록: {}", remainingChannels);

        log.info("=== Channel Repository 테스트 완료 ===");
    }

    private void testMessageRepository() {
        log.info("=== Message Repository 테스트 ===");

        User testUser = new User("test_user", "test@example.com", "password");
        Channel testChannel = new Channel(ChannelType.PUBLIC, "테스트 채널", "테스트용");

        User savedUser = userRepository.save(testUser);
        Channel savedChannel = channelRepository.save(testChannel);

        log.info("=== Message 등록 ===");
        Message message1 = new Message("안녕하세요.", savedChannel.getId(), savedUser.getId());
        Message message2 = new Message("반갑습니다.", savedChannel.getId(), savedUser.getId());

        Message savedMessage1 = messageRepository.save(message1);
        Message savedMessage2 = messageRepository.save(message2);

        log.info("생성된 메시지 1: {}", savedMessage1);
        log.info("생성된 메시지 2: {}", savedMessage2);

        log.info("=== Message 단건 조회 ===");
        Optional<Message> foundMessage = messageRepository.findById(savedMessage1.getId());
        log.info("조회된 메시지: {}", foundMessage.orElse(null));
        log.info("message1과 조회된 Message가 동일한가? : {}", savedMessage1.equals(foundMessage.orElse(null)));

        log.info("=== Message 다건 조회 ===");
        List<Message> allMessages = messageRepository.findAll();
        log.info("message 개수: {}", allMessages.size());
        log.info("전체 메시지: {}", allMessages);

        log.info("=== Message 수정 ===");
        savedMessage1.update("스프린트 미션 중");
        Message updatedMessage = messageRepository.save(savedMessage1);
        log.info("수정된 메시지: {}", updatedMessage);

        log.info("=== Message 삭제 ===");
        messageRepository.deleteById(savedMessage1.getId());
        messageRepository.deleteById(savedMessage2.getId());

        log.info("=== Message 삭제 확인 ===");
        List<Message> remainingMessages = messageRepository.findAll();
        log.info("삭제 후 메시지 목록: {}", remainingMessages);

        channelRepository.deleteById(savedChannel.getId());
        userRepository.deleteById(savedUser.getId());

        log.info("=== Message Repository 테스트 완료 ===");
    }

    private void testReadStatusRepository() {
        log.info("=== ReadStatus Repository 테스트 ===");

        User testUser = new User("test_user", "test@example.com", "password");
        Channel testChannel = new Channel(ChannelType.PUBLIC, "테스트 채널", "테스트용");

        User savedUser = userRepository.save(testUser);
        Channel savedChannel = channelRepository.save(testChannel);

        Message testMessage = new Message("테스트 메시지", savedChannel.getId(), savedUser.getId());
        Message savedMessage = messageRepository.save(testMessage);

        log.info("=== ReadStatus 등록 ===");
        ReadStatus readStatus1 = new ReadStatus(savedUser.getId(), savedMessage.getId(), Instant.now());
        ReadStatus readStatus2 = new ReadStatus(savedUser.getId(), savedMessage.getId(), Instant.now().plusSeconds(120));

        ReadStatus savedReadStatus1 = readStatusRepository.save(readStatus1);
        ReadStatus savedReadStatus2 = readStatusRepository.save(readStatus2);

        log.info("생성된 ReadStatus 1: {}", savedReadStatus1);
        log.info("생성된 ReadStatus 2: {}", savedReadStatus2);

        log.info("=== ReadStatus 단건 조회 ===");
        Optional<ReadStatus> foundReadStatus = readStatusRepository.findById(savedReadStatus1.getId());
        log.info("조회된 ReadStatus: {}", foundReadStatus.orElse(null));
        log.info("readStatus1과 조회된 ReadStatus가 동일한가? : {}", savedReadStatus1.equals(foundReadStatus.orElse(null)));

        log.info("=== ReadStatus 다건 조회 ===");
        List<ReadStatus> allReadStatuses = readStatusRepository.findAll();
        log.info("ReadStatus 개수: {}", allReadStatuses.size());
        log.info("전체 ReadStatus: {}", allReadStatuses);

        log.info("=== ReadStatus 삭제 ===");
        readStatusRepository.deleteById(savedReadStatus1.getId());
        readStatusRepository.deleteById(savedReadStatus2.getId());

        log.info("=== ReadStatus 삭제 확인 ===");
        List<ReadStatus> remainingReadStatuses = readStatusRepository.findAll();
        log.info("삭제 후 ReadStatus 목록: {}", remainingReadStatuses);

        messageRepository.deleteById(savedMessage.getId());
        channelRepository.deleteById(savedChannel.getId());
        userRepository.deleteById(savedUser.getId());

        log.info("=== ReadStatus Repository 테스트 완료 ===");
    }

    private void testBinaryContentService() {
        log.info("=== BinaryContentService 테스트 ===");

        log.info("=== BinaryContent 생성 ===");
        BinaryContentCreateRequest request = new BinaryContentCreateRequest(
                "test-image.jpg",
                "image/jpeg",
                createDummyImageData("test-image.jpg")
        );

        BinaryContentResponse createdContent = binaryContentService.create(request);
        log.info("생성된 BinaryContent: {}", createdContent);

        log.info("=== BinaryContent 조회 ===");
        BinaryContentResponse foundContent = binaryContentService.find(createdContent.getId());
        log.info("조회된 BinaryContent: {}", foundContent);

        log.info("=== BinaryContent 목록 조회 ===");
        List<BinaryContentResponse> contentList = binaryContentService.findAllByIdIn(List.of(createdContent.getId()));
        log.info("목록 조회 결과: {}", contentList.size());

        log.info("=== BinaryContent 삭제 ===");
        binaryContentService.delete(createdContent.getId());

        log.info("=== BinaryContentService 테스트 완료 ===");
    }

    private void testUserService() {
        log.info("=== UserService 테스트 ===");

        log.info("=== 사용자 생성(+프로필 이미지) ===");
        ProfileImageRequest profileImageRequest = new ProfileImageRequest(
                "woody-profile.jpg",
                "image/jpeg",
                2048L,
                createDummyImageData("woody-profile.jpg")
        );

        UserCreateRequest userRequest = new UserCreateRequest(
                "woody",
                "woody@codeit.com",
                "woody1234",
                profileImageRequest
        );

        UserResponse createdUser = userService.create(userRequest);
        log.info("생성된 사용자: {}", createdUser);

        log.info("=== 사용자 조회 ===");
        UserResponse foundUser = userService.find(createdUser.getId());
        log.info("조회된 사용자: {}", foundUser);
        log.info("온라인 상태: {}", foundUser.isOnline());

        log.info("=== 모든 사용자 조회 ===");
        List<UserResponse> allUsers = userService.findAll();
        log.info("전체 사용자 수: {}", allUsers.size());

        log.info("=== 사용자 정보 수정 ===");
        ProfileImageRequest newProfileImageRequest = new ProfileImageRequest(
                "new-profile.jpg",
                "image/jpeg",
                3072L,
                createDummyImageData("new-profile.jpg")
        );

        UserUpdateRequest updateRequest = new UserUpdateRequest(
                createdUser.getId(),
                "woody_updated",
                "woody_updated@codeit.com",
                "new_password",
                newProfileImageRequest
        );

        UserResponse updatedUser = userService.update(updateRequest);
        log.info("수정된 사용자: {}", updatedUser);

        log.info("=== 사용자 삭제 ===");
        userService.delete(createdUser.getId());

        log.info("=== UserService 테스트 완료 ===");
    }

    private void testAuthService() {
        log.info("=== AuthService 테스트 ===");

        UserCreateRequest userRequest = new UserCreateRequest(
                "login_test",
                "login@test.com",
                "test1234",
                null
        );

        UserResponse testUser = userService.create(userRequest);

        log.info("=== 로그인 테스트 ===");
        LoginRequest loginRequest = new LoginRequest("login_test", "test1234");

        try {
            LoginResponse loginUser = authService.login(loginRequest);
            log.info("로그인 성공: {}", loginUser);
        } catch (Exception e) {
            log.error("로그인 실패: {}", e.getMessage());
        }

        userService.delete(testUser.getId());

        log.info("=== AuthService 테스트 완료 ===");
    }

    private void testUserStatusService() {
        log.info("=== UserStatusService 테스트 ===");

        UserCreateRequest userRequest = new UserCreateRequest(
                "status_test",
                "status@test.com",
                "test1234",
                null
        );

        UserResponse testUser = userService.create(userRequest);

        log.info("=== 생성된 UserStatus 조회 ===");
        try {
            List<UserStatusResponse> userStatuses = userStatusService.findAll();
            UserStatusResponse createdStatus = userStatuses.stream()
                    .filter(status -> status.getUserId().equals(testUser.getId()))
                    .findFirst()
                    .orElse(null);

            if (createdStatus != null) {
                log.info("조회된 UserStatus: {}", createdStatus);
                log.info("온라인 상태: {}", createdStatus.isOnline());

                log.info("=== UserStatus ID로 단건 조회 ===");
                UserStatusResponse foundStatus = userStatusService.find(createdStatus.getId());
                log.info("ID로 조회된 UserStatus: {}", foundStatus);
            } else {
                log.warn("생성된 UserStatus를 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            log.error("UserStatus 조회 실패: {}", e.getMessage());
        }

        log.info("=== UserStatus userId로 업데이트 ===");
        UserStatusUpdateByUserRequest updateRequest = new UserStatusUpdateByUserRequest(
                testUser.getId(),
                Instant.now().plusSeconds(300)
        );

        try {
            UserStatusResponse updatedStatus = userStatusService.updateByUserId(updateRequest);
            log.info("업데이트된 UserStatus: {}", updatedStatus);
        } catch (Exception e) {
            log.error("UserStatus 업데이트 실패: {}", e.getMessage());
        }

        userService.delete(testUser.getId());

        log.info("=== UserStatusService 테스트 완료 ===");
    }

    private void testChannelService() {
        log.info("=== ChannelService 테스트 ===");

        UserCreateRequest user1Request = new UserCreateRequest(
                "channel_user1",
                "user1@channel.com",
                "test1234",
                null
        );

        UserCreateRequest user2Request = new UserCreateRequest(
                "channel_user2",
                "user2@channel.com",
                "test1234",
                null
        );

        UserResponse user1 = userService.create(user1Request);
        UserResponse user2 = userService.create(user2Request);

        log.info("=== PUBLIC 채널 생성 ===");
        PublicChannelCreateRequest publicRequest = new PublicChannelCreateRequest(
                "PUBLIC 채널",
                "PUBLIC 채널입니다."
        );

        ChannelResponse publicChannel = channelService.createPublicChannel(publicRequest);
        log.info("생성된 PUBLIC 채널: {}", publicChannel);

        log.info("=== PRIVATE 채널 생성 ===");
        PrivateChannelCreateRequest privateRequest = new PrivateChannelCreateRequest(
                List.of(user1.getId(), user2.getId())
        );

        ChannelResponse privateChannel = channelService.createPrivateChannel(privateRequest);
        log.info("생성된 PRIVATE 채널: {}", privateChannel);

        log.info("=== 사용자별 채널 목록 조회 ===");
        List<ChannelResponse> user1Channels = channelService.findAllByUserId(user1.getId());
        log.info("user1이 볼 수 있는 채널 수: {}", user1Channels.size());
        for (ChannelResponse channel : user1Channels) {
            log.info("채널: {} ({})", channel.getName(), channel.getType());
            if (channel.getType() == ChannelType.PRIVATE && channel.getParticipantUserIds() != null) {
                log.info("  - PRIVATE 채널 참여자 수: {}", channel.getParticipantUserIds().size());
                log.info("  - 참여자 ID 목록: {}", channel.getParticipantUserIds());
            }
        }
        List<ChannelResponse> user2Channels = channelService.findAllByUserId(user2.getId());
        log.info("user2가 볼 수 있는 채널 수: {}", user2Channels.size());

        for (ChannelResponse channel : user2Channels) {
            log.info("채널: {} ({})", channel.getName(), channel.getType());
            if (channel.getType() == ChannelType.PRIVATE && channel.getParticipantUserIds() != null) {
                log.info("  - PRIVATE 채널 참여자 수: {}", channel.getParticipantUserIds().size());
                log.info("  - 참여자 ID 목록: {}", channel.getParticipantUserIds());
            }
        }

        log.info("=== 채널 단건 조회 ===");
        ChannelResponse foundChannel = channelService.find(publicChannel.getId());
        log.info("조회된 채널: {}", foundChannel);

        log.info("=== 채널 수정 ===");
        ChannelUpdateRequest updateRequest = new ChannelUpdateRequest(
                publicChannel.getId(),
                "수정된 PUBLIC 채널",
                "수정된 PUBLIC 설명"
        );

        try {
            ChannelResponse updatedChannel = channelService.update(updateRequest);
            log.info("수정된 채널: {}", updatedChannel);
        } catch (Exception e) {
            log.error("채널 수정 실패: {}", e.getMessage());
        }

        log.info("=== 채널 삭제 ===");
        channelService.delete(publicChannel.getId());
        channelService.delete(privateChannel.getId());

        userService.delete(user1.getId());
        userService.delete(user2.getId());

        log.info("=== ChannelService 테스트 완료 ===");
    }

    private void testMessageService() {
        log.info("=== MessageService 테스트 ===");

        UserCreateRequest userRequest = new UserCreateRequest(
                "message_user",
                "message@test.com",
                "test1234",
                null
        );

        UserResponse testUser = userService.create(userRequest);

        PublicChannelCreateRequest channelRequest = new PublicChannelCreateRequest(
                "메시지 테스트 채널",
                "메시지 테스트용 채널"
        );

        ChannelResponse testChannel = channelService.createPublicChannel(channelRequest);

        log.info("=== 메시지 생성(+첨부파일) ===");
        List<AttachmentRequest> attachments = List.of(
                new AttachmentRequest(
                        "attachment1.pdf",
                        "application/pdf",
                        createDummyFileData("attachment1.pdf")
                ),
                new AttachmentRequest(
                        "attachment2.jpg",
                        "image/jpeg",
                        createDummyImageData("attachment2.jpg")
                )
        );

        MessageCreateRequest messageRequest = new MessageCreateRequest(
                "첨부파일이 있는 메시지입니다.",
                testChannel.getId(),
                testUser.getId(),
                attachments
        );

        MessageResponse createdMessage = messageService.create(messageRequest);
        log.info("생성된 메시지: {}", createdMessage);

        log.info("=== 채널별 메시지 목록 조회 ===");
        List<MessageResponse> channelMessages = messageService.findAllByChannelId(testChannel.getId());
        log.info("채널의 메시지 수: {}", channelMessages.size());

        log.info("=== 메시지 수정 ===");
        MessageUpdateRequest updateRequest = new MessageUpdateRequest(
                createdMessage.getId(),
                "수정된 메시지 내용"
        );

        MessageResponse updatedMessage = messageService.update(updateRequest);
        log.info("수정된 메시지: {}", updatedMessage);

        log.info("=== 메시지 삭제 ===");
        messageService.delete(createdMessage.getId());

        channelService.delete(testChannel.getId());
        userService.delete(testUser.getId());

        log.info("=== MessageService 테스트 완료 ===");
    }

    private void testReadStatusService() {
        log.info("=== ReadStatusService 테스트 ===");

        UserCreateRequest userRequest = new UserCreateRequest(
                "readstatus_user",
                "readstatus@test.com",
                "test1234",
                null
        );

        UserResponse testUser = userService.create(userRequest);

        PublicChannelCreateRequest channelRequest = new PublicChannelCreateRequest(
                "ReadStatus 테스트 채널",
                "ReadStatus 테스트용 채널"
        );

        ChannelResponse testChannel = channelService.createPublicChannel(channelRequest);

        MessageCreateRequest messageRequest = new MessageCreateRequest(
                "ReadStatus 테스트 메시지",
                testChannel.getId(),
                testUser.getId(),
                null
        );
        MessageResponse testMessage = messageService.create(messageRequest);

        log.info("=== ReadStatus 생성 ===");
        ReadStatusCreateRequest readStatusRequest = new ReadStatusCreateRequest(
                Instant.now(),
                testChannel.getId(),
                testUser.getId()
        );

        ReadStatusResponse createdReadStatus = readStatusService.create(readStatusRequest);
        log.info("생성된 ReadStatus: {}", createdReadStatus);

        log.info("=== ReadStatus 조회 ===");
        ReadStatusResponse foundReadStatus = readStatusService.find(createdReadStatus.getId());
        log.info("조회된 ReadStatus: {}", foundReadStatus);

        log.info("=== 사용자별 ReadStatus 목록 조회 ===");
        List<ReadStatusResponse> userReadStatuses = readStatusService.findAllByUserId(testUser.getId());
        log.info("사용자의 ReadStatus 수: {}", userReadStatuses.size());

        log.info("=== ReadStatus 수정 ===");
        ReadStatusUpdateRequest updateRequest = new ReadStatusUpdateRequest(
                createdReadStatus.getId(),
                Instant.now().plusSeconds(120)
        );

        ReadStatusResponse updatedReadStatus = readStatusService.update(updateRequest);
        log.info("수정된 ReadStatus: {}", updatedReadStatus);

        log.info("=== ReadStatus 삭제 ===");
        readStatusService.delete(createdReadStatus.getId());

        messageService.delete(testMessage.getId());
        channelService.delete(testChannel.getId());
        userService.delete(testUser.getId());

        log.info("=== ReadStatusService 테스트 완료 ===");
    }

    private byte[] createDummyImageData(String fileName) {
        String content = String.format("FAKE_IMAGE_DATA_FOR_%s_CREATED_AT_%d_WITH_BINARY_CONTENT",
                fileName.toUpperCase(), System.currentTimeMillis());
        return content.getBytes();
    }

    private byte[] createDummyFileData(String fileName) {
        String content = String.format("FAKE_FILE_CONTENT_FOR_%s_TIMESTAMP_%d_SIZE_CHECK",
                fileName.toUpperCase(), System.currentTimeMillis());
        return content.getBytes();
    }

}