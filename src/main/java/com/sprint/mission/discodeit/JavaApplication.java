package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

public class JavaApplication {
    private static JCFUserService userService;
    private static JCFChannelService channelService;
    private static JCFMessageService messageService;

    public static void main(String[] args) {
        userService = new JCFUserService();
        channelService = new JCFChannelService();
        messageService = new JCFMessageService();
    }
}
