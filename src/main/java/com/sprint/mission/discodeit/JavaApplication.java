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

import java.util.Optional;

public class JavaApplication {

    public static void main(String[] args) {

        UserService jcfUserService = new JCFUserService();
        ChannelService jcfChannelService = new JCFChannelService();
        MessageService jcfMessageService = new JCFMessageService(jcfUserService,jcfChannelService);

        // User Test
//        userTest(jcfUserService);
//        channelTest(jcfChannelService);
        messageTest(jcfMessageService, jcfChannelService, jcfUserService);

    }

    public static void userTest(UserService  userService) {

        // 등록
        User user1 = userService.createUser("홍길동", "1234", "자바책훔치기");
        User user2 = userService.createUser("고주몽", "1234", "자바세우기");
        User user3 = userService.createUser("이순신", "1234", "자바이기기");
        User user4 = userService.createUser("유재석", "1234", "자바의신");

        // 조회
        System.out.println("\n개별 조회 테스트 하나씩 출력");
        Optional<User> findId1 = userService.findUser(user1.getId());
        Optional<User> findId2 = userService.findUser(user2.getId());
        Optional<User> findId3 = userService.findUser(user3.getId());
        Optional<User> findId4 = userService.findUser(user4.getId());

        System.out.println(findId1.get());
        System.out.println(findId2.get());
        System.out.println(findId3.get());
        System.out.println(findId4.get());

        // 다중 조회
        System.out.println("\n다중 조회 테스트");
        System.out.println(userService.findAllUsers());

        // 수정
        userService.updateUser(user1.getId(), "김길동", "5678", "자바를 털어라");

        // 수정 테스트
        System.out.println("\nuser1 수정 테스트");
        System.out.println(findId1.get());

        // 삭제
        userService.deleteUser(user1.getId());

        // 삭제 테스트
        System.out.println("\nuser1 삭제 테스트");
        System.out.println(userService.findAllUsers());


    }

    private static void channelTest(ChannelService channelService) {

        // 등록
        Channel channel1 = channelService.createChannel("1팀");
        Channel channel2 = channelService.createChannel("2팀");
        Channel channel3 = channelService.createChannel("3팀");
        Channel channel4 = channelService.createChannel("4팀");
        Channel channel5 = channelService.createChannel("5팀");

        // 조회
        System.out.println("\n개별 조회 테스트 하나씩 출력");
        Optional<Channel> findId1 = channelService.findChannel(channel1.getId());
        Optional<Channel> findId2 = channelService.findChannel(channel2.getId());
        Optional<Channel> findId3 = channelService.findChannel(channel3.getId());
        Optional<Channel> findId4 = channelService.findChannel(channel4.getId());
        Optional<Channel> findId5 = channelService.findChannel(channel5.getId());

        System.out.println(findId1.get());
        System.out.println(findId2.get());
        System.out.println(findId3.get());
        System.out.println(findId4.get());
        System.out.println(findId5.get());

        // 다중 조회
        System.out.println("\n다중 조회 테스트");
        System.out.println(channelService.findAllChannels());

        // 수정
        channelService.updateChannel(channel1.getId(), "수정한 1팀");

        // 수정 테스트
        System.out.println("\nchannel1 수정 테스트");
        System.out.println(findId1.get());

        // 삭제
        channelService.deleteChannel(channel1.getId());

        // 삭제 테스트
        System.out.println("\nchannel1 삭제 테스트");
        System.out.println(channelService.findAllChannels());
    }


    private static void messageTest(MessageService messageService, ChannelService channelService, UserService userService) {
        System.out.println("\n===== Message Test =====");

        // 테스트를 위해 User와 Channel 생성
        User testUser = userService.createUser("테스트유저", "testpass", "테스터");
        Channel testChannel = channelService.createChannel("테스트채널");

        // 등록
        Message msg1 = messageService.createMessage("홍길동이 자바 공부를 합니다.", testChannel.getId(), testUser.getId());
        Message msg2 = messageService.createMessage("김길동이 스프링 공부를 합니다.", testChannel.getId(), testUser.getId());
        Message msg3 = messageService.createMessage("황길동이 프로젝트를 합니다.", testChannel.getId(), testUser.getId());

        // 조회
        System.out.println("\n개별 메시지 조회 테스트:");
        Optional<Message> foundMsg1 = messageService.getMessage(msg1.getId());
        Optional<Message> foundMsg2 = messageService.getMessage(msg2.getId());
        foundMsg1.ifPresent(System.out::println);
        foundMsg2.ifPresent(System.out::println);

        // 다중 조회
        System.out.println("\n모든 메시지 조회 테스트:");
        System.out.println(messageService.getAllMessages());

        // 수정
        System.out.println("\n메시지 수정 테스트 (msg1):");
        messageService.updateMessage(msg1.getId(), "김김김김길동이 메시지 내용을 수정합니다.");
        foundMsg1 = messageService.getMessage(msg1.getId()); // 수정된 내용 다시 조회
        foundMsg1.ifPresent(System.out::println);

        // 삭제
        System.out.println("\n메시지 삭제 테스트 (msg2):");
        messageService.deleteMessage(msg2.getId());

        // 삭제 후 전체 조회
        System.out.println("\n삭제 후 모든 메시지 조회:");
        System.out.println(messageService.getAllMessages());
    }

}
