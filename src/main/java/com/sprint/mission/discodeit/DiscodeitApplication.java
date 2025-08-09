package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.enums.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

import static com.sprint.mission.discodeit.entity.enums.ChannelType.PRIVATE;
import static com.sprint.mission.discodeit.entity.enums.ChannelType.PUBLIC;

@SpringBootApplication
public class DiscodeitApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(DiscodeitApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        UserRepository userRepository = new FileUserRepository();
        ChannelRepository channelRepository = new FileChannelRepository();
        MessageRepository messageRepository = new FileMessageRepository();

        UserService userService = new BasicUserService(userRepository);
        ChannelService channelService = new BasicChannelService(channelRepository);
        MessageService messageService = new BasicMessageService(messageRepository, userRepository, channelRepository);

        userCRUDTest(userService);
        channelCRUDTest(channelService);
        messageCRUDTest(userService, channelService, messageService);
    }

    private void userCRUDTest(UserService userService) throws InterruptedException {
        // 생성
        User user1 = userService.create("jae", "jae@example.com", "jae1234");
        User user2 = userService.create("hyeok", "hyeok@example.com", "hyeok1234");
        System.out.println("유저 생성: " + user1);
        System.out.println("유저 생성: " + user2);

        // 조회
        System.out.println("유저 조회(단건): " + userService.findById(user1.getId()));
        System.out.println("유저 조회(다건): " + userService.findAll().size());
        System.out.println("유저 조회(다건): " + userService.findAll());

        // 수정
        User updatedUser = userService.update(user1.getId(), null, null, "jae4321");
        System.out.println("유저 수정: " + String.join("/", updatedUser.getName(), updatedUser.getEmail(), updatedUser.getPassword()));
        System.out.println("유저 조회(단건): " + userService.findById(user1.getId()));

        // 삭제
        List<User> foundUsersAfterDelete = userService.findAll();
        userService.delete(user1.getId());
        userService.delete(user2.getId());
        System.out.println("유저 삭제: " + foundUsersAfterDelete.size());
    }

    private void channelCRUDTest(ChannelService channelService) {
        // 생성
        Channel channel1 = channelService.create("공지 채널입니다.", "공지", PUBLIC);
        Channel channel2 = channelService.create("스터디 채널입니다.", "스터디", PUBLIC);
        System.out.println("채널 생성: " + channel1.getId());
        System.out.println("채널 생성: " + channel2.getId());

        // 조회
        System.out.println("채널 조회(단건): " + channelService.findById(channel1.getId()));
        System.out.println("채널 조회(다건): " + channelService.findAll().size());
        System.out.println("채널 조회(다건): " + channelService.findAll());

        // 수정
        Channel updatedChannel = channelService.update(channel1.getId(), "공지사항", null, PRIVATE);
        System.out.println("채널 수정: " + String.join("/", updatedChannel.getName(), updatedChannel.getDescription()));
        System.out.println("채널 조회(단건): " + channelService.findById(channel1.getId()));

        // 삭제
        List<Channel> foundChannelsAfterDelete = channelService.findAll();
        channelService.delete(channel1.getId());
        channelService.delete(channel2.getId());
        System.out.println("채널 삭제: " + foundChannelsAfterDelete.size());
    }

    private void messageCRUDTest(UserService userService, ChannelService channelService, MessageService messageService) {
        // 셋업
        User user = userService.create("woody", "woody@codeit.com", "woody1234");
        Channel channel = channelService.create("공지 채널입니다.", "공지", ChannelType.PUBLIC);

        // 생성
        Message message1 = messageService.create(user.getId(), channel.getId(), "안녕하세요.");
        Message message2 = messageService.create(user.getId(), channel.getId(), "안녕하세요.");
        System.out.println("메시지 생성: " + message1.getId());
        System.out.println("메시지 생성: " + message2.getId());

        // 조회
        System.out.println("메시지 조회(단건): " + messageService.findById(message1.getId()));
        System.out.println("메시지 조회(다건): " + messageService.findAll().size());
        System.out.println("메시지 조회(다건): " + messageService.findAll());

        // 수정
        Message updatedMessage = messageService.update(message1.getId(), "반갑습니다.");
        System.out.println("메시지 수정: " + updatedMessage.getContent());
        System.out.println("메시지 조회(단건): " + messageService.findById(message1.getId()));

        // 메시지 삭제
        List<Message> foundMessagesAfterDelete = messageService.findAll();
        messageService.delete(message1.getId());
        messageService.delete(message2.getId());
        System.out.println("메시지 삭제: " + foundMessagesAfterDelete.size());

        // 채널 삭제
        List<Channel> foundChannelsAfterDelete = channelService.findAll();
        channelService.delete(channel.getId());
        System.out.println("채널 삭제: " + foundChannelsAfterDelete.size());

        // 유저 삭제
        List<User> foundUsersAfterDelete = userService.findAll();
        userService.delete(user.getId());
        System.out.println("유저 삭제: " + foundUsersAfterDelete.size());
    }
}