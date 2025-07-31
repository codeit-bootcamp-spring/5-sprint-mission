package com.sprint.mission.discodeit;


import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.repository.file.*;
import com.sprint.mission.discodeit.repository.jcf.*;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.basic.*;
import com.sprint.mission.discodeit.service.jcf.*;

import java.util.*;


public class JavaApplication {

    private static UserRepository userRepositoryJCF = new JCFUserRepository();
    private static UserService userServiceJCF = new BasicUserService(userRepositoryJCF);

    private static UserRepository userRepositoryFile = new FileUserRepository();
    private static UserService userServiceFile = new BasicUserService(userRepositoryFile);

    private static MessageRepository messageRepositoryJCF = new JCFMessageRepository();
    private static MessageService messageServiceJCF = new BasicMessageService(messageRepositoryJCF);

    private static MessageRepository messageRepositoryFile = new FileMessageRepository();
    private static MessageService messageServiceFile = new BasicMessageService(messageRepositoryFile);

    private static ChannelRepository channelRepositoryJCF = new JCFChannelRepository();
    private static ChannelService channelServiceJCF = new BasicChannelService(channelRepositoryJCF);

    private static ChannelRepository channelRepositoryFile = new FileChannelRepository();
    private static ChannelService channelServiceFile = new BasicChannelService(channelRepositoryFile);



    public static void main(String[] args) {

        // JCF 저장 방식
        // userCRUDTest((BasicUserService)userServiceJCF);
        // messageCRUDTest((BasicMessageService)messageServiceJCF);
        channelCRUDTest((BasicChannelService)channelServiceJCF);


        // 파일 저장 방식
        // userCRUDTest((BasicUserService)userServiceJCF);
        // messageCRUDTest((BasicMessageService) messageServiceFile);
        // channelCRUDTest((BasicChannelService) channelServiceFile);

    }

    public static void userCRUDTest(BasicUserService userService){

        // 생성
        System.out.println("---------------------------");
        System.out.println("User 5명 생성이 완료되었습니다.");
        System.out.println("---------------------------");

        User user1 = userService.createUser("Woody");
        User user2 = userService.createUser("Alice");
        User user3 = userService.createUser("Bob");
        User user4 = userService.createUser("Eve");
        User user5 = userService.createUser("Malary");
        System.out.println("유저 생성: " + user1);
        System.out.println("유저 생성: " + user2);
        System.out.println("유저 생성: " + user3);
        System.out.println("유저 생성: " + user4);
        System.out.println("유저 생성: " + user5);
        System.out.println();

        // 단건 조회
        Optional<User> findUser = userService.getUser(user1.getId());
        System.out.println("찾은 사람 : " + findUser + "\n");

        // 사용자 이름 수정
        System.out.println("수정 전 이름 : " + user1.getUsername() + ", 생성시간 : " + user1.getCreatedAt());
        user1.update("Buzz");
        userService.updateUser(user1.getId(), user1); // Woody -> Buzz 으로 이름 변경
        System.out.println("수정 후 이름 : " + user1.getUsername() + ", 수정시간 : " + user1.getUpdatedAt());
        System.out.println();

        // 모든 사용자 조회
        System.out.println("====== 전체 유저 조회  =====");
        List<User> userList = userService.getAllUsers();
        System.out.println("총 유저 수 : " + userList.size());
        for (User user : userList) {
            System.out.println(user);
        }
        System.out.println();


        // 사용자 삭제
        System.out.println("====== 유저 삭제 ======");
        userService.deleteUser(user2.getId());
        System.out.println("삭제된 유저 이름 : " + user2.getUsername());
        System.out.println();
        System.out.println("-- 삭제 후 전체 유저 조회 --");
        List<User> userList1 = userService.getAllUsers();
        System.out.println("현재 유저 수 : " + userList1.size());
        for (User user : userList1) {
            System.out.println(user);
        }
        System.out.println();

        // 사용자 존재 여부 확인
        System.out.println("====== 유저 존재 여부 확인  ======");
        boolean result = userService.existsById(user2.getId());
        System.out.println("'" + user2.getUsername() + "' 의 존재 여부 확인 : " + result);
        boolean result1 = userService.existsById(user3.getId());
        System.out.println("'" + user3.getUsername() + "' 의 존재 여부 확인 : " + result1);

    }


    public static void messageCRUDTest(BasicMessageService messageService){

        System.out.println("---------------------------");
        System.out.println(" ✅ 메세지 3건 생성이 완료되었습니다.");
        System.out.println("---------------------------");
        System.out.println();

        Message message1 = messageService.createMessage("Happy Birthday");
        Message message2 = messageService.createMessage("Hello Java World!");
        Message message3 = messageService.createMessage("Good Morning");
        System.out.println("메세지 생성 : " + message1.getContent());
        System.out.println("메세지 생성 : " + message2.getContent());
        System.out.println("메세지 생성 : " + message3.getContent());
        System.out.println();


        // 단건 조회
        Optional<Message> findMessage = messageService.getMessage(message1.getId());
        System.out.println("찾은 메세지 : " + findMessage.get().getContent() + "\n");

        // 메세지 내용 수정
        System.out.println("수정 전 내용 : " + message1.getContent() + ", 생성시간 : " + message1.getCreatedAt());
        message1.update("Merry Christmas");
        messageService.updateMessage(message1.getId(),"Merry Christmas"); // Happy Birthday -> Merry Christmas 으로 메세지 변경
        System.out.println("수정 후 내용 : " + message1.getContent() + ", 수정시간 : " + message1.getUpdatedAt());
        System.out.println();

        // 모든 메세지 조회
        System.out.println("====== 전체 메세지 조회  =====");
        List<Message> messageList = messageService.getAllMessages();
        System.out.println("총 Message 개수 : " + messageList.size());
        for (Message message : messageList) {
            System.out.println(message);
        }
        System.out.println();


        // 메세지 삭제
        System.out.println("** 메세지가 삭제되었습니다 **");
        messageService.deleteMessage(message3.getId());
        System.out.println("✅ 삭제된 Message 내용 : " + message3.getContent() + "/n");

        System.out.println("-- 삭제 후 전체 Message 조회 --");
        List<Message> messageList1 = messageService.getAllMessages();
        System.out.println("현재 Message 개수 : " + messageList1.size());
        for (Message message : messageList1) {
            System.out.println(message);
        }
        System.out.println();

        // 메세지 존재 여부 확인
        System.out.println("====== 메세지 존재 여부 확인  ======");
        boolean result = messageService.existsById(message2.getId());
        System.out.println("'" + message2.getContent() + "' 의 존재 여부 확인 : " + result);
        boolean result1 = messageService.existsById(message3.getId());
        System.out.println("'" + message3.getContent() + "' 의 존재 여부 확인 : " + result1);
    }

    public static void channelCRUDTest(BasicChannelService channelService){

        System.out.println("---------------------------");
        System.out.println("✅ 채널 4개 생성이 완료되었습니다.");
        System.out.println("---------------------------");
        System.out.println();

        Channel channel1 = channelService.createChannel("공지");
        Channel channel2 = channelService.createChannel("문의");
        Channel channel3 = channelService.createChannel("학습");
        Channel channel4 = channelService.createChannel("스터디");
        System.out.println("채널1 생성 : " + channel1.getChannelname());
        System.out.println("채널2 생성 : " + channel2.getChannelname());
        System.out.println("채널3 생성 : " + channel3.getChannelname());
        System.out.println("채널4 생성 : " + channel4.getChannelname());
        System.out.println();


        // 채널 단건 조회
        Optional<Channel> findChannel = channelService.getChannel(channel2.getId());
        System.out.println("찾은 채널 : " + findChannel.get().getChannelname() + "\n");


        // 채널 제목 수정
        System.out.println("수정 전 채널명 : " + channel2.getChannelname() + ", 생성시간 : " + channel2.getCreatedAt());
        channelService.updateChannel(channel2.getId(), "자유"); // 학습 -> 자유 로 채널명 변경
        System.out.println("수정 후 채널명 : " + channel2.getChannelname() + ", 수정시간 : " + channel2.getUpdatedAt());
        System.out.println();

        // 모든 채널 조회
        System.out.println("====== 전체 채널 조회  =====");
        List<Channel> channelList = channelService.getAllChannels();
        System.out.println("총 채널 개수 : " + channelList.size());
        for (Channel channel : channelList) {
            System.out.println(channel);
        }
        System.out.println();

        // 채널 삭제
        System.out.println("** 채널3(학습)이 삭제되었습니다 **");
        channelService.deleteChannel(channel3.getId());
        System.out.println("삭제된 Channel 이름 : " + channel3.getChannelname() + "/n");
        System.out.println("-- 삭제 후 전체 전체 조회 --");
        List<Channel> ChannelList1 = channelService.getAllChannels();
        System.out.println("현재 Channel 개수 : " + ChannelList1.size());
        for (Channel channel : ChannelList1) {
            System.out.println(channel);
        }
    }
}
