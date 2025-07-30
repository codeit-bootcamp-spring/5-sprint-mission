package com.sprint.mission.discodeit.config;


import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

public class AppConfig {

    public UserRepository userRepository(){
        return new JCFUserRepository();
    }

    public MessageRepository messageRepository(){
        return new JCFMessageRepository();
    }

    public ChannelRepository channelRepository(){
        return new JCFChannelRepository();
    }

    public UserService userService(){
        return new JCFUserService(channelService(), userRepository());
    }

    public ChannelService channelService(){
        return new JCFChannelService(channelRepository(), userService());
    }

    public MessageService messageService(){
        return new JCFMessageService(messageRepository(), userService(), channelService());
    }

}
