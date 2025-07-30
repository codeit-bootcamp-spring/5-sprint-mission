package com.sprint.mission.discodeit.service.factory;

import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

public class ServiceFactory {

    public static UserService createUserService() {
        return JCFUserService.getInstance();
    }

    public static ChannelService createChannelService() {
        return JCFChannelService.getInstance();
    }

    public static MessageService createMessageService() {
        return JCFMessageService.getInstance();
    }
}
