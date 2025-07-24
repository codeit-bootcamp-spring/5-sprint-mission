package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.List;
import java.util.UUID;

public class JavaApplication {

    public static void userTest() {
        UserService jcfu = new JCFUserService();
        System.out.println("----- user create -----");
        User user1 = jcfu.createUser("kkk@kkk.com", "james", "1234", "#4756", UserStatus.ONLINE);
        User user2 = jcfu.createUser("jjj@jjj.com", "john", "3454", "#3132", UserStatus.ONLINE);
        User user3 = jcfu.createUser("sss@sss.com", "kim", "1133", "#5666", UserStatus.OFFLINE);
        User user4 = jcfu.createUser("ttt@ttk.com", "park", "1564", "#4786", UserStatus.DND);
        User user5 = jcfu.createUser("kyy@yyk.com", "elis", "1777", "#9876", UserStatus.IDLE);

        System.out.println(user1.toString());
        System.out.println(user2.toString());
        System.out.println(user3.toString());
        System.out.println(user4.toString());
        System.out.println(user5.toString());


        User findU1 = null;
        try {
            System.out.println("----- user find -----");
            findU1 = jcfu.findById(user3.getId());
            System.out.println(findU1.toString());
            // 파라미터를 randomUUID()로 생성하여 대입하거나, null 값 대입
//            User findU2 = jcfu.findById(UUID.randomUUID());  // [Error] User not found
//            User findU2 = jcfu.findById(null); // [Error] User id is wrong
//            System.out.println(findU2.toString());
            System.out.println("----- all users find -----");
            List<User> allUsers =  jcfu.findAll();
            allUsers.forEach(System.out::println);

            System.out.println("----- user delete -----");
            User delU1 = jcfu.deleteById(findU1.getId());
            // 파라미터를 randomUUID()로 생성하여 대입하거나, null 값 대입
//            User delU1 = jcfu.deleteById(UUID.randomUUID()); // [Error] User does not already exist.
//            User delU1 = jcfu.deleteById(null); // [Error] User id is null.
            System.out.println(delU1.toString());

            System.out.println("----- user update -----");
            System.out.println("Change Value[userId, email, name, PW, discrimnator, status]: " + user4.getId() + ", jjj@jjj.net, ryu, 9999, #5567" + UserStatus.OFFLINE);
            System.out.println("Before update: " + user4.toString());
            User updateU1 = jcfu.update(user4.getId(), "jjj@jjj.net", "ryu", "9999", "#5567", UserStatus.OFFLINE);
            System.out.println(updateU1.toString());

        } catch(NullPointerException | IllegalArgumentException e) {
            System.out.println("[Error] " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void channelTest() {
        UserService jcfu = new JCFUserService();
        ChannelService jcfc = new JCFChannelService();

        System.out.println("------------ user create ------------");
        User user1 = jcfu.createUser("kkk@kkk.com", "james", "1234", "#4756", UserStatus.ONLINE);
        User user2 = jcfu.createUser("jjj@jjj.com", "john", "3454", "#3132", UserStatus.ONLINE);
        User user3 = jcfu.createUser("sss@sss.com", "kim", "1133", "#5666", UserStatus.OFFLINE);
        User user4 = jcfu.createUser("ttt@ttk.com", "park", "1564", "#4786", UserStatus.DND);
        User user5 = jcfu.createUser("kyy@yyk.com", "elis", "1777", "#9876", UserStatus.IDLE);

        System.out.println("user 1: " + user1.toString());
        System.out.println("user 2: " + user2.toString());
        System.out.println("user 3: " + user3.toString());
        System.out.println("user 4: " + user4.toString());
        System.out.println("user 5: " + user5.toString());


        try {
            System.out.println("------------- channel create ------------");
            Channel chnl1 = jcfc.createChannel(user1.getId(), "BE-SPRING", ChannelType.COMMON,false);
//            Channel chnl1 = jcfc.createChannel(null, "BE-SPRING", false); // [Error] A channel object is empty.
            Channel chnl2 = jcfc.createChannel(user3.getId(), "Chickens", ChannelType.PERSONAL,false);
            Channel chnl3 = jcfc.createChannel(user4.getId(), "Movies", ChannelType.PERSONAL,true);

            System.out.println("channel 1:\n" + chnl1.toString());
            System.out.println("\nchannel 2:\n" + chnl2.toString());
            System.out.println("\nchannel 3:\n" + chnl3.toString());

            System.out.println("------------ find channel  ------------");
            Channel findCh2 = jcfc.findById(chnl2.getId());
            System.out.println("channel 2 : " + chnl2.toString());
            System.out.println("search ch 2 : " + findCh2.toString());
            System.out.println("------------ find all channel  ------------");
            List<Channel> channels =  jcfc.findAll();
            channels.forEach((o) -> System.out.println(o.getChannelName()+ " : " + o.toString()));
            System.out.println("------------ delete channel  ------------");
            Channel delCh3 = jcfc.deleteById(chnl3.getId());
//            Channel delCh3 = jcfc.deleteById(null);
//            Channel delCh3 = jcfc.deleteById(UUID.randomUUID());
            System.out.println("deleted channel : " + delCh3);
            System.out.println("channel list : ");
            channels.forEach((o) -> System.out.println(o.getChannelName()+ " : " + o.toString()));

            System.out.println("------------ update channel  ------------");
            System.out.println("Change Value[ChannelId, UserId, ChannelName, nsfw]: " + chnl2.getId() + ", " + user5.getId() + ", IntelliJ" + ", " + true);
            System.out.println("Before updating : " + chnl2.toString());
            jcfc.update(chnl2.getId(), user5.getId(), "IntelliJ", true);
            System.out.println("update " + chnl2.getChannelName() + " : " + chnl2.toString());
        } catch (NullPointerException | IllegalArgumentException e) {
            System.out.println("[Error] " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void messageTest() {
        MessageService jcfm = new JCFMessageService();
        UserService jcfu = new JCFUserService();
        ChannelService jcfc = new JCFChannelService();

        System.out.println("------------ user create ------------");
        User user1 = jcfu.createUser("kkk@kkk.com", "james", "1234", "#4756", UserStatus.ONLINE);
        User user2 = jcfu.createUser("jjj@jjj.com", "john", "3454", "#3132", UserStatus.ONLINE);
        User user3 = jcfu.createUser("sss@sss.com", "kim", "1133", "#5666", UserStatus.OFFLINE);
        User user4 = jcfu.createUser("ttt@ttk.com", "park", "1564", "#4786", UserStatus.DND);
        User user5 = jcfu.createUser("kyy@yyk.com", "elis", "1777", "#9876", UserStatus.IDLE);

        System.out.println("user 1: " + user1.toString());
        System.out.println("user 2: " + user2.toString());
        System.out.println("user 3: " + user3.toString());
        System.out.println("user 4: " + user4.toString());
        System.out.println("user 5: " + user5.toString());

        try {
            System.out.println("------------- channel create ------------");
            Channel chnl1 = jcfc.createChannel(user1.getId(), "BE-SPRING", ChannelType.PERSONAL,false);
            Channel chnl2 = jcfc.createChannel(user3.getId(), "Chickens", ChannelType.COMMON,false);
            Channel chnl3 = jcfc.createChannel(user4.getId(), "Movies", ChannelType.COMMON,true);

            System.out.println("channel 1:\n" + chnl1.toString());
            System.out.println("\nchannel 2:\n" + chnl2.toString());
            System.out.println("\nchannel 3:\n" + chnl3.toString());

            System.out.println("-------------------------------------------------");
            System.out.println("-------------------------------------------------");
            System.out.println("------------ Message Test Start!!  ------------");
            System.out.println("-------------------------------------------------");
            System.out.println("-------------------------------------------------");
            System.out.println("------------ create Message  ------------");
            Message msg1 = jcfm.createMessage(chnl1, "안녕하세요, 여러분!", user3.getId(), false);
            Message msg2 = jcfm.createMessage(chnl1, "오늘 날씨 어때요?", user2.getId(), false);
            Message msg3 = jcfm.createMessage(chnl1, "나빠요.", user2.getId(), false);
            Message msg4 = jcfm.createMessage(chnl2, "저메추 부탁드립니다.", user5.getId(), true);
            Message msg5 = jcfm.createMessage(chnl2, "치킨이요.", user3.getId(), true);
            // 메세지가 null이거나 "" 일 경우, [Error] : message is null or empty.
//            Message msg5 = jcfm.createMessage(chnl2, "", null, true);
            // channel 안에 메세지를 작성하는 user가 없을 경우, [Error] : author is not in the channel.
//            Message msg1 = jcfm.createMessage(chnl1, "Hello, World!", user4, false);
            System.out.println("msg 1:" + msg1.toString());
            System.out.println("msg 2:" + msg2.toString());
            System.out.println("msg 3:" + msg3.toString());
            System.out.println("msg 4:" + msg4.toString());
            System.out.println("msg 5:" + msg5.toString());

            System.out.println("------------ find Message  ------------");
            Message findMsg1 = jcfm.findById(msg2.getId());
//            Message findMsg1 = jcfm.findById(null); // [Error] : message id is null.
//            Message findMsg1 = jcfm.findById(UUID.randomUUID());  // [Error] : message id not found.
            System.out.println("find Message1 : "  + findMsg1.toString());
            System.out.println("------------ find All Message  ------------");
            List<Message> messages = jcfm.findAll();
            messages.forEach(System.out::println);
            System.out.println("------------ delete Message  ------------");
            Message delMsg1 = jcfm.deleteById(msg4.getId());
            System.out.println("delete Message1 : "  + delMsg1.toString());
//            Message delMsg2 = jcfm.deleteById(msg4.getId());  // data에서 삭제한 후 호출 - [Error] : message is not found.
//            Message delMsg2 = jcfm.deleteById(null); // [Error] : message id is null.
//            System.out.println("delete Message2 : "  + delMsg2.toString());
            System.out.println("------------ update Message  ------------");
            System.out.println("before updated message : " + msg3.toString());
            Message updatedMsg1 = jcfm.update(msg3.getId(), "좋아요.", true);
            System.out.println("updated MessageDTO1 : " + updatedMsg1.toString());


        } catch (IllegalArgumentException | NullPointerException e) {
            System.out.println("[Error] : "+ e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
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
