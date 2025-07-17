package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.List;

public class JavaApplication {
    private static final UserService userService = new JCFUserService();
    private static final ChannelService channelService = new JCFChannelService();
    private static final MessageService messageService = new JCFMessageService();

    public static void main(String[] args) {
        userCRUDTest();
        channelCRUDTest();
        messageCRUDTest();
    }

    private static void userCRUDTest() {
        System.out.print("============User CRUD Test============\n");

        userService.createUser("aaa@aaa.aaa", "just_do_it", "홍길동", "1234", "010-1234-5678");
        userService.createUser("bbb@bbb.bbb", "what_can_i_do", "홍길동", "1234", "010-1234-5678");
        userService.createUser("ccc@ccc.ccc", "anything", "이길동", "1234", "010-1234-5678");
        userService.createUser("ddd@ddd.ddd", "omg", "이길동", "1234", "010-1234-5678");
        userService.createUser("eee@eee.eee", "this_code", "김길동", "1234", "010-1234-5678");

        if (userService.createUser("aaa@aaa.aaa", "just_do_it", "홍길동", "1234", "010-1234-5678")) {
            System.out.print("User Created Successfully\n");
        } else System.out.print("User Creation Failed\n");


        User findByEmail = userService.findByEmail("aaa@aaa.aaa");
        if (findByEmail == null) System.out.print("findByEmail not found\n");
        else System.out.print("findByEmail user found\n" + findByEmail);


        User findByUserName = userService.findByUserName("길동");
        if (findByUserName == null) System.out.print("findByUserName not found\n");
        else System.out.print(findByUserName);


        List<User> findByNickname = userService.findByNickName("대소동");
        if (findByNickname == null || findByNickname.isEmpty()) System.out.print("users not found\n");
        else {
            for (User user : findByNickname) {
                System.out.print(user);
            }
        }

        String email = "ddd@ddd.ddd";
        if (userService.updateByEmail(email, "holymoly", "동동동", "4321", "010-0000-1111")) {
            System.out.print(": User Updated Successfully\n");
        } else System.out.print(": User Updated Failed\n");


        if (userService.deleteByEmail(email)) System.out.print(": User Deleted Successfully\n");
        else System.out.print(": User Deleted Failed\n");


        for (User user : userService.findAllUsers()) {
            System.out.print(user);
        }
    }

    private static void channelCRUDTest() {
        System.out.print("============Channel CRUD Test============\n");

        channelService.createChannel("소개합니다", "개성 가득한 소개를 남겨주세요.");
        channelService.createChannel("소통할까요", "다양한 주제로 자유롭게 소통하는 채널이에요.");
        channelService.createChannel("공지", "이 채널에 공유되는 소식은 꼭 확인해주세요.");

        Channel testChannel = channelService.createChannel("테스트 채널", "테스트 전용 채널입니다");

        String searchWord = "테스트";
        List<Channel> findChannels = channelService.findByChannelName(searchWord);
        if (findChannels == null || findChannels.isEmpty()) System.out.print("channels not found\n");
        else {
            System.out.print("------ 검색어: " + searchWord + " ------\n");
            for (Channel channel : findChannels) {
                System.out.print(channel);
                System.out.print("--------------\n");
            }
        }

        channelService.updateById(testChannel.getId(), "TEST CHANNEL", "테스트 전용 채널입니다.");
        System.out.print("------ 모든 채널 확인 -------\n");
        for (Channel channel : channelService.findAllChannels()) {
            System.out.print(channel);
            System.out.print("--------------\n");
        }

        channelService.deleteById(testChannel.getId());
        Channel findChannel = channelService.findById(testChannel.getId());
        if (findChannel == null) System.out.print("channel not found\n");
        else System.out.print(findChannel);

    }

    private static void messageCRUDTest() {
        System.out.print("============Message CRUD Test============\n");

        userService.createUser("test1@test.com", "send_user", "주건희", "test", "010-1111-2222");
        userService.createUser("test2@test.com", "receive_user", "받건희", "test", "010-2222-3333");

        Channel testChannel1 = channelService.createChannel("메세지 테스트 채널1", "테스트용 채널입니다.");
        Channel testChannel2 = channelService.createChannel("메세지 테스트 채널2", "테스트용 채널입니다.");

        User sendUser = userService.findByUserName("send_user");
        Message testMessage = messageService.createMessage(sendUser, testChannel1, "심심하다");
        messageService.createMessage(sendUser, testChannel1, "나랑 같이 떠들 사람");
        messageService.createMessage(userService.findByUserName("receive_user"), testChannel2, "들썩 들썩 떠들썩");

        System.out.print("----- 메세지 검색 -----\n");
        for (Message m : messageService.findByUser(sendUser)) {
            System.out.print(m);
            System.out.print("-------------------------\n");
        }

        String updateMessage = "안 심심하다";
        if (messageService.updateMessage(testMessage.getId(), sendUser, testChannel1, updateMessage)) {
            System.out.print("Message Updated Successfully\n");
        } else System.out.print("Message Updated Failed\n");

        for (Message m : messageService.searchByMessage(updateMessage)) {
            System.out.print(m);
            System.out.print("------------------------\n");
        }

        if (messageService.deleteMessage(testMessage.getId(), sendUser, testChannel1)) {
            System.out.print("Message Deleted Successfully\n");
        } else System.out.print("Message Deleted Failed\n");

        for (Message m : messageService.findByUser(sendUser)) {
            System.out.print(m);
            System.out.print("------------------------\n");
        }
    }
}
