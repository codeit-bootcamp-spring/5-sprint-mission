package com.sprint.mission.discodeit.config;


import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

public class AppConfig {

    public UserService userService(){
        JCFUserService userService = JCFUserService.getInstance();

        userService.setChannelService(JCFChannelService.getInstance());
        return userService;
    }

    public ChannelService channelService(){
        JCFChannelService channelService = JCFChannelService.getInstance();
        return channelService;
    }

    public MessageService messageService(){
        JCFMessageService messageService = JCFMessageService.getInstance();
        return messageService;
    }

}
