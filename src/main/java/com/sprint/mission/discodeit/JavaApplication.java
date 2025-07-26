package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.List;

public class JavaApplication {
    private static final StringBuilder log = new StringBuilder();
    private static final JCFUserService userService = new JCFUserService();
    private static final JCFChannelService channelService = new JCFChannelService();
    private static final JCFMessageService messageService = new JCFMessageService();

    public static void main(String[] args) {
        userCRUDTest();
        channelCRUDTest();
        messageCRUDTest();

        System.out.println(log);
    }

    private static void userCRUDTest() {
        log.append("============User CRUD Test============\n");

        userService.createUser("aaa@aaa.aaa", "just_do_it", "홍길동", "1234", "010-1234-5678");
        userService.createUser("bbb@bbb.bbb", "what_can_i_do", "홍길동", "1234", "010-1234-5678");
        userService.createUser("ccc@ccc.ccc", "anything", "이길동", "1234", "010-1234-5678");
        userService.createUser("ddd@ddd.ddd", "omg", "이길동", "1234", "010-1234-5678");
        userService.createUser("eee@eee.eee", "this_code", "김길동", "1234", "010-1234-5678");

        if (userService.createUser("aaa@aaa.aaa", "just_do_it", "홍길동", "1234", "010-1234-5678"))
        {
            log.append("User Created Successfully\n");
        }
        else log.append("User Creation Failed\n");


        User findByEmail = userService.findByEmail("aaa@aaa.aaa");
        if (findByEmail == null) log.append("findByEmail not found\n");
        else log.append("findByEmail user found\n").append(findByEmail);


        User findByUserName = userService.findByUserName("길동");
        if (findByUserName == null) log.append("findByUserName not found\n");
        else log.append(findByUserName);


        List<User> findByNickname = userService.findByNickName("홍길동");
        if (findByNickname == null) log.append("users not found\n");
        else
        {
            for (User user : findByNickname)
            {
                log.append(user);
            }
        }

        String email = "ddd@ddd.ddd";
        if (userService.updateByEmail(email, "holymoly", "동동동", "4321", "010-0000-1111"))
        {
            log.append(email).append(": User Updated Successfully\n");
        }
        else log.append(email).append(": User Updated Failed\n");


        if (userService.deleteByEmail(email)) log.append(email).append(": User Deleted Successfully\n");
        else log.append(email).append(": User Deleted Failed\n");


        for (User user : userService.findAllUsers())
        {
            log.append(user);
        }
    }

    private static void channelCRUDTest() {
        log.append("============Channel CRUD Test============\n");

        channelService.createChannel("소개합니다", "개성 가득한 소개를 남겨주세요.");
        channelService.createChannel("소통할까요", "다양한 주제로 자유롭게 소통하는 채널이에요.");
        channelService.createChannel("공지", "이 채널에 공유되는 소식은 꼭 확인해주세요.");

        Channel testChannel = channelService.createChannel("테스트 채널", "테스트 전용 채널입니다");

        String searchWord = "테스트";
        List<Channel> findChannels = channelService.findByChannelName(searchWord);
        if (findChannels == null) log.append("channels not found\n");
        else
        {
            log.append("------ 검색어: ").append(searchWord).append(" ------").append('\n');
            for (Channel channel : findChannels)
            {
                log.append(channel);
                log.append("--------------\n");
            }
        }

        channelService.updateById(testChannel.getId(), "TEST CHANNEL", "테스트 전용 채널입니다.");
        log.append("------ 모든 채널 확인 -------\n");
        for (Channel channel : channelService.findAllChannels())
        {
            log.append(channel);
            log.append("--------------\n");
        }

        channelService.deleteById(testChannel.getId());
        Channel findChannel = channelService.findById(testChannel.getId());
        if (findChannel == null) log.append("channel not found\n");
        else log.append(findChannel);

    }

    private static void messageCRUDTest() {
        log.append("============Message CRUD Test============\n");

        userService.createUser("test1@test.com", "send_user", "주건희", "test", "010-1111-2222");
        userService.createUser("test2@test.com", "receive_user", "받건희", "test", "010-2222-3333");

        Channel testChannel1 = channelService.createChannel("메세지 테스트 채널1", "테스트용 채널입니다.");
        Channel testChannel2 = channelService.createChannel("메세지 테스트 채널2", "테스트용 채널입니다.");

        User sendUser = userService.findByUserName("send_user");
        Message testMessage = messageService.createMessage(sendUser, testChannel1, "심심하다");
        messageService.createMessage(sendUser, testChannel1, "나랑 같이 떠들 사람");
        messageService.createMessage(userService.findByUserName("receive_user"), testChannel2, "들썩 들썩 떠들썩");

        log.append("----- 메세지 검색 -----\n");
        for (Message m : messageService.findByUser(sendUser))
        {
            log.append(m);
            log.append("-------------------------\n");
        }

        String updateMessage = "안 심심하다";
        if (messageService.updateMessage(testMessage.getId(), sendUser, testChannel1, updateMessage))
        {
            log.append("Message Updated Successfully\n");
        }
        else log.append("Message Updated Failed\n");

        for (Message m : messageService.searchByMessage(updateMessage))
        {
            log.append(m);
            log.append("------------------------\n");
        }

        if (messageService.deleteMessage(testMessage.getId(), sendUser, testChannel1))
        {
            log.append("Message Deleted Successfully\n");
        }
        else log.append("Message Deleted Failed\n");

        for (Message m : messageService.findByUser(sendUser))
        {
            log.append(m);
            log.append("------------------------\n");
        }
    }
}
