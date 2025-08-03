package com.sprint.mission;

import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.test.ChannelServiceTest;
import com.sprint.mission.discodeit.test.MessageServiceTest;
import com.sprint.mission.discodeit.test.UserServiceTest;

public class JavaApplication {
    public static void main(String[] args) {

        UserRepository userRepository = new FileUserRepository();
        UserService userService = new FileUserService(userRepository);
        UserServiceTest userServiceTest = new UserServiceTest(userService);
        userServiceTest.runAllTest();
        System.out.println("-----------------------------------------\n");

        ChannelRepository channelRepository = new FileChannelRepository();
        ChannelService channelService = new FileChannelService(channelRepository);
        ChannelServiceTest channelServiceTest = new ChannelServiceTest(channelService);
        channelServiceTest.runAllTest();
        System.out.println("-----------------------------------------\n");

        MessageRepository messageRepository = new FileMessageRepository();
        MessageService messageService = new FileMessageService(messageRepository);
        MessageServiceTest messageServiceTest = new MessageServiceTest(messageService, userService, channelService);
        messageServiceTest.runAllTest();
    }
}