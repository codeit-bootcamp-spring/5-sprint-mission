package com.sprint.mission;

import com.sprint.mission.discodeit.test.ChannelServiceTest;
import com.sprint.mission.discodeit.test.MessageServiceTest;
import com.sprint.mission.discodeit.test.UserServiceTest;

public class JavaApplication {
    public static void main(String[] args) {
        UserServiceTest userServiceTest = new UserServiceTest();
        userServiceTest.runAllTest();
        System.out.println("-----------------------------------------\n");

        ChannelServiceTest channelServiceTest = new ChannelServiceTest();
        channelServiceTest.runAllTest();
        System.out.println("-----------------------------------------\n");

        MessageServiceTest messageServiceTest = new MessageServiceTest();
        messageServiceTest.runAllTest();
    }
}