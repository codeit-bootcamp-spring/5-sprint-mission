package com.sprint.mission.discodeit.config;


import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

public class AppConfig {

    public UserService userService(){
        UserService userService = new JCFUserService();
        return userService;
    }

    public ChannelService channelService(){
        ChannelService channelService = new JCFChannelService();
        return channelService;
    }

    public MessageService messageService(){
        MessageService messageService = new JCFMessageService();
        return messageService;
    }

}
