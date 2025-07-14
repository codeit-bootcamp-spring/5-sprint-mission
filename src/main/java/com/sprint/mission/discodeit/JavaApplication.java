package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

public class JavaApplication {
    public static void main(String[] args) {
        JCFUserService us = new JCFUserService();
        JCFChannelService cs = new JCFChannelService();
        JCFMessageService ms = new JCFMessageService();

        // 생성
        us.createUser(new User("testUser1"));
        us.createUser(new User("testUser2"));
        us.createUser(new User("testuser3"));
        cs.createChannel(new Channel("testChannel1"));
        cs.createChannel(new Channel("testChannel2"));
        cs.createChannel(new Channel("testchannel3"));
        ms.createMessage(new Message("test Message1", us.searchByIndex(0).getId()));
        ms.createMessage(new Message("test Message2", us.searchByIndex(0).getId()));
        ms.createMessage(new Message("test message3", us.searchByIndex(1).getId()));

        // 조회
        System.out.println(us.searchByIndex(0));
        System.out.println(cs.searchByIndex(0));
        System.out.println(ms.searchByIndex(0));
        System.out.println("이름 조회 테스트 시작\n" + us.searchByName("User").toString().replace("}, ", "},\n"));
        System.out.println(cs.searchByName("Channel").toString().replace("}, ", "},\n"));
        System.out.println(ms.searchByContent("Mess").toString().replace("}, ", "},\n"));
        System.out.println("테스트 끝");
        System.out.println(us.searchById(us.searchByIndex(0).getId()));
        System.out.println(cs.searchById(cs.searchByIndex(0).getId()));
        System.out.println(ms.searchById(ms.searchByIndex(0).getId()));
        System.out.println("---------------------------------------------------------------------------------");

        // 모두 조회
        System.out.println(us.getAllUsers().toString().replace("}, ", "},\n"));
        System.out.println(cs.getAllChannels().toString().replace("}, ", "},\n"));
        System.out.println(ms.getAllMessages().toString().replace("}, ", "},\n"));
        System.out.println("---------------------------------------------------------------------------------");

        // 수정 및 조회
        us.searchByIndex(0).addChannel(cs.searchByIndex(0));
        cs.searchByIndex(0).addUser(us.searchByIndex(0));
        us.updateUser(us.searchByIndex(0).updateName("updatedName"));
        cs.updateChannel(cs.searchByIndex(0).updateName("updatedName"));
        ms.updateMessage(ms.searchByIndex(0).updateContent("updatedContent"));
        System.out.println(us.searchByIndex(0));
        System.out.println(cs.searchByIndex(0));
        System.out.println(ms.searchByIndex(0));
        System.out.println("---------------------------------------------------------------------------------");

        // 삭제 및 조회
        us.deleteUser(us.searchByIndex(1));
        cs.deleteChannel(cs.searchByIndex(0));
        ms.deleteMessage(ms.searchByIndex(0));

        System.out.println(us.getAllUsers().toString().replace("}, ", "},\n"));
        System.out.println(cs.getAllChannels().toString().replace("}, ", "},\n"));
        System.out.println(ms.getAllMessages().toString().replace("}, ", "},\n"));
    }
}
