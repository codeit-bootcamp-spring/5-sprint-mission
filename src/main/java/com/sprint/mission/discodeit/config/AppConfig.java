package com.sprint.mission.discodeit.config;


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

public class AppConfig {
    public ChannelRepository channelRepository = new FileChannelRepository();
    public UserRepository userRepository = new FileUserRepository();
    public MessageRepository messageRepository = new FileMessageRepository();

    public UserService userService(){
        return new BasicUserService(this.userRepository, this.channelService());
    }

    public ChannelService channelService(){
        return new BasicChannelService(this.channelRepository);
    }

    public MessageService messageService(){
        return new BasicMessageService(this.messageRepository);
    }
}
