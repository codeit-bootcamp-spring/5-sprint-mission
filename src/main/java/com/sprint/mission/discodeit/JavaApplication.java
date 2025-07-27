package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.List;
import java.util.UUID;

public class JavaApplication {

    public static void userTest() {
        UserService us = new FileUserService();

        System.out.println("----------- create users ---------------");
        User user1 = us.createUser("kkk@kkk.com", "james", "1234", "#4756", UserStatus.ONLINE);
        User user2 = us.createUser("jjj@jjj.com", "john", "3454", "#3132", UserStatus.ONLINE);
        User user3 = us.createUser("sss@sss.com", "kim", "1133", "#5666", UserStatus.OFFLINE);
        User user4 = us.createUser("ttt@ttk.com", "park", "1564", "#4786", UserStatus.DND);
        User user5 = us.createUser("kyy@yyk.com", "elis", "1777", "#9876", UserStatus.IDLE);

        System.out.println("user 1: " + user1.toString());
        System.out.println("user 2: " + user2.toString());
        System.out.println("user 3: " + user3.toString());
        System.out.println("user 4: " + user4.toString());
        System.out.println("user 5: " + user5.toString());

        System.out.println("----------- find users ---------------");
        User findU3 = us.findById(user3.getId());
        System.out.println("find user3 : " + findU3.toString());
        User findU4 = us.findById(user4.getId());
        System.out.println("find user4 : " + findU4.toString());

        System.out.println("----------- read all users ---------------");
        List<User> users = us.findAll();
        users.forEach(System.out::println);


        System.out.println("----------- delete users ---------------");
        User delU5 = us.deleteById(user5.getId());
        System.out.println("delete user5 : " + delU5.toString());


        System.out.println("----------- update users ---------------");
        System.out.println("Change Value[userId, email, name, PW, discrimnator, status]: " + user4.getId() + ", jjj@jjj.net, ryu, 9999, #5567" + UserStatus.OFFLINE);
        System.out.println("Before update : " + user4.toString());
        User updateU4 = us.update(user4.getId(), "jjj@jjj.net", "ryu", "9999", "#5567", UserStatus.OFFLINE);
        System.out.println("After update : "+ updateU4.toString());
        System.out.println("Check update : " + us.findById(user4.getId()));
    }

    public static void channelTest() {
        ChannelService cs = new FileChannelService();

        System.out.println("------------- create users ----------------");
        User user1 = new User("kkk@kkk.com", "james", "1234", "#4756", UserStatus.ONLINE);
        User user2 = new User("jjj@jjj.com", "john", "3454", "#3132", UserStatus.ONLINE);
        User user3 = new User("sss@sss.com", "kim", "1133", "#5666", UserStatus.OFFLINE);
        User user4 = new User("ttt@ttk.com", "park", "1564", "#4786", UserStatus.DND);
        User user5 = new User("kyy@yyk.com", "elis", "1777", "#9876", UserStatus.IDLE);

        System.out.println("user 1: " + user1.toString());
        System.out.println("user 2: " + user2.toString());
        System.out.println("user 3: " + user3.toString());
        System.out.println("user 4: " + user4.toString());
        System.out.println("user 5: " + user5.toString());

        System.out.println("------------ create channels ---------------");
        Channel chnl1 = cs.createChannel(user1.getId(), "BE-SPRING", ChannelType.COMMON,false);
        Channel chnl2 = cs.createChannel(user3.getId(), "Chickens", ChannelType.PERSONAL,false);
        Channel chnl3 = cs.createChannel(user4.getId(), "Movies", ChannelType.PERSONAL,true);

        System.out.println("channel 1:\n" + chnl1.toString());
        System.out.println("\nchannel 2:\n" + chnl2.toString());
        System.out.println("\nchannel 3:\n" + chnl3.toString());

        System.out.println("------------ find channels ---------------");
        Channel findCh2 = cs.findById(chnl2.getId());
        System.out.println("search ch 2 : " + findCh2.toString());

        System.out.println("------------ read all channels ---------------");
        List<Channel> channels =  cs.findAll();
        channels.forEach(System.out::println);

        System.out.println("------------ delete channels ---------------");
        Channel delCh3 = cs.deleteById(chnl3.getId());
        System.out.println("delete ch 3 : " + delCh3.toString());

        System.out.println("------------ update channels ---------------");
        System.out.println("Change Value[ChannelId, UserId, ChannelName, nsfw]: " + chnl2.getId() + ", " + user5.getId() + ", IntelliJ" + ", " + true);
        System.out.println("Before updating : " + chnl2.toString());
        Channel updateC2 = cs.update(chnl2.getId(), user5.getId(), "IntelliJ", true);
        System.out.println("After update : " + updateC2.toString());
        System.out.println("Check update : " + cs.findById(chnl2.getId()).toString());
    }

    public static void messageTest() {
        MessageService ms = new FileMessageService();

        System.out.println("------------- create users ----------------");
        User user1 = new User("kkk@kkk.com", "james", "1234", "#4756", UserStatus.ONLINE);
        User user2 = new User("jjj@jjj.com", "john", "3454", "#3132", UserStatus.ONLINE);
        User user3 = new User("sss@sss.com", "kim", "1133", "#5666", UserStatus.OFFLINE);
        User user4 = new User("ttt@ttk.com", "park", "1564", "#4786", UserStatus.DND);
        User user5 = new User("kyy@yyk.com", "elis", "1777", "#9876", UserStatus.IDLE);

        System.out.println("user 1: " + user1.toString());
        System.out.println("user 2: " + user2.toString());
        System.out.println("user 3: " + user3.toString());
        System.out.println("user 4: " + user4.toString());
        System.out.println("user 5: " + user5.toString());

        System.out.println("------------ create channels ---------------");
        Channel chnl1 = new Channel(user1.getId(), "BE-SPRING", ChannelType.COMMON,false);
        Channel chnl2 = new Channel(user3.getId(), "Chickens", ChannelType.PERSONAL,false);
        Channel chnl3 = new Channel(user4.getId(), "Movies", ChannelType.PERSONAL,true);

        System.out.println("channel 1:\n" + chnl1.toString());
        System.out.println("\nchannel 2:\n" + chnl2.toString());
        System.out.println("\nchannel 3:\n" + chnl3.toString());

        System.out.println("------------ create messages ---------------");
        Message msg1 = ms.createMessage(chnl1, "안녕하세요, 여러분!", user3.getId(), false);
        Message msg2 = ms.createMessage(chnl1, "오늘 날씨 어때요?", user2.getId(), false);
        Message msg3 = ms.createMessage(chnl1, "나빠요.", user2.getId(), false);
        Message msg4 = ms.createMessage(chnl2, "저메추 부탁드립니다.", user5.getId(), true);
        Message msg5 = ms.createMessage(chnl2, "치킨이요.", user3.getId(), true);

        System.out.println("msg 1:" + msg1.toString());
        System.out.println("msg 2:" + msg2.toString());
        System.out.println("msg 3:" + msg3.toString());
        System.out.println("msg 4:" + msg4.toString());
        System.out.println("msg 5:" + msg5.toString());

        System.out.println("------------ find messages ---------------");
        Message findMsg2 = ms.findById(msg2.getId());
        System.out.println("find Message2 : "  + findMsg2.toString());

        System.out.println("------------ read all messages ---------------");
        List<Message> msgs = ms.findAll();
        msgs.forEach(System.out::println);

        System.out.println("------------ delete messages ---------------");
        Message delMsg4 = ms.deleteById(msg4.getId());
        System.out.println("delete Message4 : "  + delMsg4.toString());

        System.out.println("------------ update messages ---------------");
        System.out.println("before updated message : " + msg3.toString());
        Message updatedMsg3 = ms.update(msg3.getId(), "좋아요.", true);
        System.out.println("updated Message3 : " + updatedMsg3.toString());
        System.out.println("checking Message3 : " + ms.findById(msg3.getId()).toString());

    }

    public static void main(String[] args) {
        System.out.println("****** JCF*Service 구현체를 File*Service 구현체로 교체  *********");
        System.out.println("========================== User Test Start =========================");
        System.out.println("====================================================================");
        System.out.println("====================================================================");
        userTest();

        System.out.println("====================================================================");
        System.out.println("====================================================================");
        System.out.println("====================================================================");
        System.out.println("========================== Channel Test Start =========================");
        System.out.println("====================================================================");
        System.out.println("====================================================================");
        System.out.println("====================================================================");
        channelTest();

        System.out.println("====================================================================");
        System.out.println("====================================================================");
        System.out.println("====================================================================");
        System.out.println("========================== message Test Start =========================");
        System.out.println("====================================================================");
        System.out.println("====================================================================");
        System.out.println("====================================================================");
        messageTest();
    }
}
