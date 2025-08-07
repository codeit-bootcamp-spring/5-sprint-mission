package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.repository.file.*;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

import java.util.List;

public class JavaApplication {
    static User setupUser(UserService userService) {
        User user = userService.create("woody", "woody@codeit.com", "woody1234");
        return user;
    }

    static Channel setupChannel(ChannelService channelService) {
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("공지", "공지 채널입니다.");
        return channelService.createPublicChannel(request);
    }

    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
        List<BinaryContentCreateRequest> attachments = List.of();
        MessageCreateRequest request = new MessageCreateRequest("안녕하세요.", channel.getId(), author.getId(), attachments);
        var message = messageService.create(request);
        System.out.println("메시지 생성: " + message.getId());
    }

    public static void main(String[] args) {

        String fileDirectory = ".discodeit";

        // 레포지토리 초기화
        UserRepository userRepository = new FileUserRepository(fileDirectory);
        ChannelRepository channelRepository = new FileChannelRepository(fileDirectory);
        MessageRepository messageRepository = new FileMessageRepository(fileDirectory);
        BinaryContentRepository binaryContentRepository = new FileBinaryContentRepository(fileDirectory);
        ReadStatusRepository readStatusRepository = new FileReadStatusRepository(fileDirectory);
        UserStatusRepository userStatusRepository = new FileUserStatusRepository(fileDirectory);

        // 서비스 초기화
        UserService userService = new BasicUserService(userRepository, binaryContentRepository, userStatusRepository);
        ChannelService channelService = new BasicChannelService(channelRepository, readStatusRepository, userRepository, messageRepository);
        MessageService messageService = new BasicMessageService(messageRepository, channelRepository, userRepository, binaryContentRepository);

        // 셋업
        User user = setupUser(userService);
        Channel channel = setupChannel(channelService);
        // 테스트
        messageCreateTest(messageService, channel, user);
    }
}
