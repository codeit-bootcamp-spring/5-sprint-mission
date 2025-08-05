package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

import java.util.List;

public class JavaApplication {

    public static void userTest(UserService userService) {
        System.out.println("----------- create users ---------------");
        User user1 = userService.createUser("kkk@kkk.com", "james", "1234", "#4756", UserStatus.ONLINE);
        User user2 = userService.createUser("jjj@jjj.com", "john", "3454", "#3132", UserStatus.ONLINE);
        User user3 = userService.createUser("sss@sss.com", "kim", "1133", "#5666", UserStatus.OFFLINE);
        User user4 = userService.createUser("ttt@ttk.com", "park", "1564", "#4786", UserStatus.DND);
        User user5 = userService.createUser("kyy@yyk.com", "elis", "1777", "#9876", UserStatus.IDLE);

        System.out.println("user 1: " + user1.toString());
        System.out.println("user 2: " + user2.toString());
        System.out.println("user 3: " + user3.toString());
        System.out.println("user 4: " + user4.toString());
        System.out.println("user 5: " + user5.toString());

        System.out.println("----------- find users ---------------");
        User findU3 = userService.findById(user3.getId());
        System.out.println("find user3 : " + findU3.toString());
        User findU4 = userService.findById(user4.getId());
        System.out.println("find user4 : " + findU4.toString());

        System.out.println("----------- read all users ---------------");
        List<User> users = userService.findAll();
        users.forEach(System.out::println);


        System.out.println("----------- delete users ---------------");
        User delU5 = userService.deleteById(user5.getId());
        System.out.println("delete user5 : " + delU5.toString());


        System.out.println("----------- update users ---------------");
        System.out.println("Change Value[userId, email, name, PW, discrimnator, status]: " + user4.getId() + ", jjj@jjj.net, ryu, 9999, #5567" + UserStatus.OFFLINE);
        System.out.println("Before update : " + user4.toString());
        User updateU4 = userService.update(user4.getId(), "jjj@jjj.net", "ryu", "9999", "#5567", UserStatus.OFFLINE);
        System.out.println("After update : "+ updateU4.toString());
        System.out.println("Check update : " + userService.findById(user4.getId()));

        users = userService.findAll();
        users.forEach(u -> userService.deleteById(u.getId()));
    }

    public static void channelTest(ChannelService channelService, UserRepository userRepository) {
        System.out.println("------------- create users ----------------");
        User user1 = userRepository.save(new User("kkk@kkk.com", "james", "1234", "#4756", UserStatus.ONLINE));
        User user2 = userRepository.save(new User("jjj@jjj.com", "john", "3454", "#3132", UserStatus.ONLINE));
        User user3 = userRepository.save(new User("sss@sss.com", "kim", "1133", "#5666", UserStatus.OFFLINE));
        User user4 = userRepository.save(new User("ttt@ttk.com", "park", "1564", "#4786", UserStatus.DND));
        User user5 = userRepository.save(new User("kyy@yyk.com", "elis", "1777", "#9876", UserStatus.IDLE));

        System.out.println("user 1: " + user1.toString());
        System.out.println("user 2: " + user2.toString());
        System.out.println("user 3: " + user3.toString());
        System.out.println("user 4: " + user4.toString());
        System.out.println("user 5: " + user5.toString());

        System.out.println("------------ create channels ---------------");
        Channel chnl1 = channelService.createChannel(user1.getId(), "BE-SPRING", ChannelType.COMMON,false);
        Channel chnl2 = channelService.createChannel(user3.getId(), "Chickens", ChannelType.PERSONAL,false);
        Channel chnl3 = channelService.createChannel(user4.getId(), "Movies", ChannelType.PERSONAL,true);

        System.out.println("channel 1:\n" + chnl1.toString());
        System.out.println("\nchannel 2:\n" + chnl2.toString());
        System.out.println("\nchannel 3:\n" + chnl3.toString());

        System.out.println("------------ find channels ---------------");
        Channel findCh2 = channelService.findById(chnl2.getId());
        System.out.println("search ch 2 : " + findCh2.toString());

        System.out.println("------------ read all channels ---------------");
        List<Channel> channels =  channelService.findAll();
        channels.forEach(System.out::println);

        System.out.println("------------ delete channels ---------------");
        Channel delCh3 = channelService.deleteById(chnl3.getId());
        System.out.println("delete ch 3 : " + delCh3.toString());

        System.out.println("------------ update channels ---------------");
        System.out.println("Change Value[ChannelId, UserId, ChannelName, nsfw]: " + chnl2.getId() + ", " + user5.getId() + ", IntelliJ" + ", " + true);
        System.out.println("Before updating : " + chnl2.toString());
        Channel updateC2 = channelService.update(chnl2.getId(), user5.getId(), "IntelliJ", true);
        System.out.println("After update : " + updateC2.toString());
        System.out.println("Check update : " + channelService.findById(chnl2.getId()).toString());

        channels = channelService.findAll();
        channels.forEach(c -> channelService.deleteById(c.getId()));
        List<User> users = userRepository.findAll();
        users.forEach(u -> userRepository.delete(u.getId()));
    }

    public static void messageTest(MessageService messageService, UserRepository userRepository, ChannelRepository channelRepository) {
        System.out.println("------------- create users ----------------");
        User user1 = userRepository.save(new User("kkk@kkk.com", "james", "1234", "#4756", UserStatus.ONLINE));
        User user2 = userRepository.save(new User("jjj@jjj.com", "john", "3454", "#3132", UserStatus.ONLINE));
        User user3 = userRepository.save(new User("sss@sss.com", "kim", "1133", "#5666", UserStatus.OFFLINE));
        User user4 = userRepository.save(new User("ttt@ttk.com", "park", "1564", "#4786", UserStatus.DND));
        User user5 = userRepository.save(new User("kyy@yyk.com", "elis", "1777", "#9876", UserStatus.IDLE));

        System.out.println("user 1: " + user1.toString());
        System.out.println("user 2: " + user2.toString());
        System.out.println("user 3: " + user3.toString());
        System.out.println("user 4: " + user4.toString());
        System.out.println("user 5: " + user5.toString());

        System.out.println("------------ create channels ---------------");
        Channel chnl1 = channelRepository.save(new Channel(user1.getId(), "BE-SPRING", ChannelType.COMMON,false));
        Channel chnl2 = channelRepository.save(new Channel(user3.getId(), "Chickens", ChannelType.PERSONAL,false));
        Channel chnl3 = channelRepository.save(new Channel(user4.getId(), "Movies", ChannelType.PERSONAL,true));

        System.out.println("channel 1:\n" + chnl1.toString());
        System.out.println("\nchannel 2:\n" + chnl2.toString());
        System.out.println("\nchannel 3:\n" + chnl3.toString());

        System.out.println("------------ create messages ---------------");
        Message msg1 = messageService.createMessage(chnl1, "안녕하세요, 여러분!", user3.getId(), false);
        Message msg2 = messageService.createMessage(chnl1, "오늘 날씨 어때요?", user2.getId(), false);
        Message msg3 = messageService.createMessage(chnl1, "나빠요.", user2.getId(), false);
        Message msg4 = messageService.createMessage(chnl2, "저메추 부탁드립니다.", user5.getId(), true);
        Message msg5 = messageService.createMessage(chnl2, "치킨이요.", user3.getId(), true);

        System.out.println("msg 1:" + msg1.toString());
        System.out.println("msg 2:" + msg2.toString());
        System.out.println("msg 3:" + msg3.toString());
        System.out.println("msg 4:" + msg4.toString());
        System.out.println("msg 5:" + msg5.toString());

        System.out.println("------------ find messages ---------------");
        Message findMsg2 = messageService.findById(msg2.getId());
        System.out.println("find Message2 : "  + findMsg2.toString());

        System.out.println("------------ read all messages ---------------");
        List<Message> msgs = messageService.findAll();
        msgs.forEach(System.out::println);

        System.out.println("------------ delete messages ---------------");
        Message delMsg4 = messageService.deleteById(msg4.getId());
        System.out.println("delete Message4 : "  + delMsg4.toString());

        System.out.println("------------ update messages ---------------");
        System.out.println("before updated message : " + msg3.toString());
        Message updatedMsg3 = messageService.update(msg3.getId(), "좋아요.", true);
        System.out.println("updated Message3 : " + updatedMsg3.toString());
        System.out.println("checking Message3 : " + messageService.findById(msg3.getId()).toString());

        List<User> users = userRepository.findAll();
        users.forEach(u -> userRepository.delete(u.getId()) );
        List<Channel> channels = channelRepository.findAll();
        channels.forEach((c) -> channelRepository.delete(c.getId()) );
        msgs = messageService.findAll();
        msgs.forEach((m) -> messageService.deleteById(m.getId()) );
    }

    public static void main(String[] args) {
        UserService userService = new BasicUserService();
        UserRepository userRepository = new FileUserRepository();
        ChannelService channelService = new BasicChannelService(userRepository);
        ChannelRepository channelRepository = new FileChannelRepository();
        MessageService messageService = new BasicMessageService(userRepository, channelRepository);

        System.out.println("****** Basic*Service 테스트 *********");
        System.out.println("====================================================================");
        System.out.println("========================== User Test Start =========================");
        System.out.println("====================================================================");
        userTest(userService);

        System.out.println("====================================================================");
        System.out.println("========================== Channel Test Start =========================");
        System.out.println("====================================================================");
        channelTest(channelService, userRepository);

        System.out.println("====================================================================");
        System.out.println("========================== message Test Start =========================");
        System.out.println("====================================================================");
        messageTest(messageService, userRepository, channelRepository);
    }
}
