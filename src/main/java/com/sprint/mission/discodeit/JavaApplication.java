package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.Timer;

public class JavaApplication {
    public static void main(String[] args) {
        JCFUserService us = new JCFUserService();
        JCFChannelService cs = new JCFChannelService();
        JCFMessageService ms = new JCFMessageService();

        Timer timer = new Timer();

        us.addUser(new User("testUser1"));
        us.addUser(new User("testUser2"));
        us.addUser(new User("testUser3"));
        cs.addChannel(new Channel("testChannel1"));
        cs.addChannel(new Channel("testChannel2"));
        cs.addChannel(new Channel("testChannel3"));
        ms.addMessage(new Message("test Message1", us.getUser(0).getId()));
        ms.addMessage(new Message("test Message2", us.getUser(0).getId()));
        ms.addMessage(new Message("test Message3", us.getUser(1).getId()));

        System.out.println(us.getUser(0));
        System.out.println(cs.getChannel(0));
        System.out.println(ms.getMessage(0));
        System.out.println("---------------------------------------------------------------------------------");

        System.out.println(us.getAllUsers().toString().replace("}, ", "},\n"));
        System.out.println(cs.getAllChannels().toString().replace("}, ", "},\n"));
        System.out.println(ms.getAllMessages().toString().replace("}, ", "},\n"));
        System.out.println("---------------------------------------------------------------------------------");

        us.getUser(0).addChannel(cs.getChannel(0));
        cs.getChannel(0).addUser(us.getUser(0));
        us.updateUser(us.getUser(0).updateName("updatedName"));
        cs.updateChannel(cs.getChannel(0).updateName("updatedName"));
        ms.updateMessage(ms.getMessage(0).updateContent("updatedContent"));
        System.out.println(us.getUser(0));
        System.out.println(cs.getChannel(0));
        System.out.println(ms.getMessage(0));
        System.out.println("---------------------------------------------------------------------------------");

        us.deleteUser(us.getUser(1));
        cs.deleteChannel(cs.getChannel(0));
        ms.deleteMessage(ms.getMessage(0));

        System.out.println(us.getAllUsers().toString().replace("}, ", "},\n"));
        System.out.println(cs.getAllChannels().toString().replace("}, ", "},\n"));
        System.out.println(ms.getAllMessages().toString().replace("}, ", "},\n"));
    }
}
