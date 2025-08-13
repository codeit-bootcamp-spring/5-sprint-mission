package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.auth.request.LoginRequest;
import com.sprint.mission.discodeit.dto.channel.data.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.message.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.user.data.UserDto;
import com.sprint.mission.discodeit.dto.user.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.user.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.main.Channel;
import com.sprint.mission.discodeit.entity.main.Message;
import com.sprint.mission.discodeit.entity.main.User;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.repository.file.*;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicAuthService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@SpringBootApplication
public class DiscodeitApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

/*
        UserRepository userRepository = new JCFUserRepository();
        ChannelRepository channelRepository = new JCFChannelRepository();
        MessageRepository messageRepository = new JCFMessageRepository();
*/

        UserRepository userRepository = new FileUserRepository();
        ChannelRepository channelRepository = new FileChannelRepository();
        MessageRepository messageRepository = new FileMessageRepository();

        BinaryContentRepository binaryContentRepository = new FileBinaryContentRepository();
        UserStatusRepository userStatusRepository = new FileUserStatusRepository();

        UserService userService = new BasicUserService(userRepository, binaryContentRepository, userStatusRepository);
        AuthService authService = new BasicAuthService(userRepository);
        ChannelService channelService = context.getBean(ChannelService.class);
        MessageService messageService = context.getBean(MessageService.class);

        userCRUDTest(userService);
        authTest(authService, userService);
        channelCRUDTest(channelService);
        messageCRUDTest(userService, channelService, messageService);
    }

    private static void userCRUDTest(UserService userService) {
        // 생성
        UserCreateRequest userCreateRequest1 = new UserCreateRequest("jae", "jae@example.com", "jae1234");
        UserCreateRequest userCreateRequest2 = new UserCreateRequest("hyeok", "hyeok@example.com", "hyeok1234");

        Optional<BinaryContentCreateRequest> binaryContentCreateRequest1 =
                Optional.of(new BinaryContentCreateRequest("file", "image/jpeg", new byte[]{}));

        Optional<BinaryContentCreateRequest> binaryContentCreateRequest2 =
                Optional.of(new BinaryContentCreateRequest("file", "image/jpeg", new byte[]{}));

        User user1 = userService.create(userCreateRequest1, binaryContentCreateRequest1);
        User user2 = userService.create(userCreateRequest2, binaryContentCreateRequest2);
        System.out.println("유저 생성: " + user1);
        System.out.println("유저 생성: " + user2);

        // 조회
        System.out.println("유저 조회(단건): " + userService.find(user1.getId()));
        System.out.println("유저 조회(다건): " + userService.findAll().size());
        System.out.println("유저 조회(다건): " + userService.findAll());

        // 수정 (UserUpdateRequest 사용)
        UserUpdateRequest updateRequest =
                new UserUpdateRequest("jaeNew", "jaeNew@example.com", "jae4321");

        User updatedUser = userService.update(user1.getId(), updateRequest, Optional.empty());
        System.out.println("유저 수정: " + String.join("/", updatedUser.getUsername(), updatedUser.getEmail(), updatedUser.getPassword()));
        System.out.println("유저 조회(단건): " + userService.find(user1.getId()));

        // 삭제
        List<UserDto> foundUsersAfterDelete = userService.findAll();
        userService.delete(user1.getId());
        userService.delete(user2.getId());
        System.out.println("유저 삭제: " + foundUsersAfterDelete.size());
    }

    private static void authTest(AuthService authService, UserService userService) {
        UserCreateRequest userCreateRequest1 = new UserCreateRequest("jae", "jae@example.com", "jae1234");

        Optional<BinaryContentCreateRequest> binaryContentCreateRequest1 =
                Optional.of(new BinaryContentCreateRequest("file", "image/jpeg", new byte[]{}));

        User user = userService.create(userCreateRequest1, binaryContentCreateRequest1);

        String username = user.getUsername();
        String password = user.getPassword();

        // 로그인 케이스 테스트
        testLogin(authService, new LoginRequest(username, password), "정상 로그인");
        testLogin(authService, new LoginRequest(username, "wrong"), "비밀번호 오류");
        testLogin(authService, new LoginRequest("unknown", password), "아이디 없음");

        // 테스트 끝나면 유저 삭제
        userService.delete(user.getId());
        System.out.println("현재 유저 수: " + userService.findAll().size());
    }

    private static void testLogin(AuthService authService, LoginRequest request, String caseName) {
        try {
            authService.login(request);
            System.out.println(caseName + " → 성공");
        } catch (NoSuchElementException e) {
            System.out.println(caseName + " → 존재하지 않는 유저");
        } catch (IllegalArgumentException e) {
            System.out.println(caseName + " → 아이디 또는 비밀번호 오류");
        }
    }

    private static void channelCRUDTest(ChannelService channelService) {
        // 생성 - PublicChannelCreateRequest 사용
        PublicChannelCreateRequest publicReq1 =
                new PublicChannelCreateRequest("공지 채널입니다.", "공지");
        PublicChannelCreateRequest publicReq2 =
                new PublicChannelCreateRequest("스터디 채널입니다.", "스터디");

        Channel channel1 = channelService.create(publicReq1);
        Channel channel2 = channelService.create(publicReq2);

        System.out.println("채널 생성: " + channel1.getId());
        System.out.println("채널 생성: " + channel2.getId());

        // 조회
        System.out.println("채널 조회(단건): " + channelService.find(channel1.getId()));
        System.out.println("채널 조회(다건): " + channelService.findAllByUserId(null).size());
        System.out.println("채널 조회(다건): " + channelService.findAllByUserId(null));

        // 수정 - PublicChannelUpdateRequest 사용
        PublicChannelUpdateRequest updateReq =
                new PublicChannelUpdateRequest("공지사항", null);
        Channel updatedChannel = channelService.update(channel1.getId(), updateReq);

        System.out.println("채널 수정: " +
                String.join("/", updatedChannel.getName(), updatedChannel.getDescription()));
        System.out.println("채널 조회(단건): " + channelService.find(channel1.getId()));

        // 삭제
        List<ChannelDto> foundChannelsAfterDelete = channelService.findAllByUserId(null);
        channelService.delete(channel1.getId());
        channelService.delete(channel2.getId());
        System.out.println("채널 삭제: " + foundChannelsAfterDelete.size());
    }

    private static void messageCRUDTest(UserService userService, ChannelService channelService, MessageService messageService) {
        // 셋업
        UserCreateRequest userCreateRequest1 =
                new UserCreateRequest("jae", "jae@example.com", "jae1234");

        Optional<BinaryContentCreateRequest> binaryContentCreateRequest1 =
                Optional.of(new BinaryContentCreateRequest("file", "image/jpeg", new byte[]{}));

        User user = userService.create(userCreateRequest1, binaryContentCreateRequest1);

        // 채널 생성 - PublicChannelCreateRequest 사용
        PublicChannelCreateRequest publicChannelReq =
                new PublicChannelCreateRequest("공지 채널입니다.", "공지");
        Channel channel = channelService.create(publicChannelReq);

        // 메시지 생성 - MessageCreateRequest + 첨부파일 리스트
        MessageCreateRequest messageReq1 =
                new MessageCreateRequest("안녕하세요.", channel.getId(), user.getId());
        MessageCreateRequest messageReq2 =
                new MessageCreateRequest("안녕하세요.", channel.getId(), user.getId());

        List<BinaryContentCreateRequest> attachments = List.of(); // 첨부 없을 경우 빈 리스트

        Message message1 = messageService.create(messageReq1, attachments);
        Message message2 = messageService.create(messageReq2, attachments);

        System.out.println("메시지 생성: " + message1.getId());
        System.out.println("메시지 생성: " + message2.getId());

        // 메시지 조회
        System.out.println("메시지 조회(단건): " + messageService.find(message1.getId()));
        System.out.println("메시지 조회(채널별): " + messageService.findAllByChannelId(channel.getId()).size());
        System.out.println("메시지 조회(채널별): " + messageService.findAllByChannelId(channel.getId()));

        // 메시지 수정 - MessageUpdateRequest 사용
        MessageUpdateRequest updateReq = new MessageUpdateRequest("반갑습니다.");
        Message updatedMessage = messageService.update(message1.getId(), updateReq);
        System.out.println("메시지 수정: " + updatedMessage.getContent());
        System.out.println("메시지 조회(단건): " + messageService.find(message1.getId()));

        // 메시지 삭제
        List<Message> foundMessagesAfterDelete = messageService.findAllByChannelId(channel.getId());
        messageService.delete(message1.getId());
        messageService.delete(message2.getId());
        System.out.println("메시지 삭제: " + foundMessagesAfterDelete.size());

        // 채널 삭제 - findAllByUserId 사용
        List<ChannelDto> foundChannelsAfterDelete = channelService.findAllByUserId(user.getId());
        channelService.delete(channel.getId());
        System.out.println("채널 삭제: " + foundChannelsAfterDelete.size());

        // 유저 삭제
        List<UserDto> foundUsersAfterDelete = userService.findAll();
        userService.delete(user.getId());
        System.out.println("유저 삭제: " + foundUsersAfterDelete.size());
    }
}