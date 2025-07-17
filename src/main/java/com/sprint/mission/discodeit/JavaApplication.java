package com.sprint.mission.discodeit;


import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.jcf.*;

import java.util.*;


public class JavaApplication {
    private static JCFUserSerivce userService = new JCFUserSerivce();;
    private static JCFMessageService messageService = new JCFMessageService();
    private static JCFChannelService channelService = new JCFChannelService();


    public static void main(String[] args) {

        //userCRUDTest(userService);
        //messageCRUDTest(messageService);
        channelCRUDTest(channelService);
    }

    public static void userCRUDTest(JCFUserSerivce userService){

        System.out.println("---------------------------");
        System.out.println("User 5명 생성이 완료되었습니다.");
        System.out.println("---------------------------");
        System.out.println();

        User user1 = userService.createUser("양의지");
        User user2 = userService.createUser("김택연");
        User user3 = userService.createUser("정수빈");
        User user4 = userService.createUser("오명진");
        User user5 = userService.createUser("박치국");

        // 사용자 단건 조회
        System.out.println("====== User 단건 조회  =====");
        userService.getUser(user1.getId());
        System.out.println(user1);
        System.out.println();

        // 사용자 이름 수정
        System.out.println("====== User1 이름 수정  =====");
        System.out.println("수정 전 이름 : " + user1.getUsername() + ", 생성시간 : " + user1.getCreatedAt());
        userService.updateUser(user1.getId(), "BBBB"); // 양의지 -> BBBB 으로 이름 변경
        System.out.println("수정 후 이름 : " + user1.getUsername() + ", 수정시간 : " + user1.getUpdatedAt());
        System.out.println();

        // 모든 사용자 조회
        System.out.println("====== 전체 User 조회  =====");
        List<User> userList = userService.getAllUsers();
        System.out.println("총 User 수 : " + userList.size());
        for (User user : userList) {
            System.out.println(user);
        }
        System.out.println();

        // 사용자 삭제
        System.out.println("====== User 삭제 ======");
        userService.deleteUser(user1.getId());
        System.out.println("삭제된 User 이름 : " + user1.getUsername());
        System.out.println("-- 삭제 후 전체 User 조회 --");
        List<User> userList1 = userService.getAllUsers();
        System.out.println("현재 User 수 : " + userList1.size());
        for (User user : userList1) {
            System.out.println(user);
        }
    }


    public static void messageCRUDTest(JCFMessageService messageService){

        System.out.println("---------------------------");
        System.out.println("Message 3건 생성이 완료되었습니다.");
        System.out.println("---------------------------");
        System.out.println();

        Message message1 = messageService.createMessage("내일 두산 베어스 경기한다");
        Message message2 = messageService.createMessage("오 이기겠는데??");
        Message message3 = messageService.createMessage("이번에 4연전이라 경기 운영 잘해야 할듯!");

        // 메세지 단건 조회
        System.out.println("====== Message 단건 조회  =====");
        messageService.getMessage(message1.getId());
        System.out.println(message1);
        System.out.println();

        // 메세지 내용 수정
        System.out.println("====== Message2 이름 수정  =====");
        System.out.println("수정 전 내용 : " + message2.getContent() + ", 생성시간 : " + message2.getCreatedAt());
        messageService.updateMessage(message2.getId(), "내일 비온다는데?!?!?"); // "오 이기겠는데?? -> 내일 비온다는데?!?! 으로 이름 변경
        System.out.println("수정 후 내용 : " + message2.getContent() + ", 수정시간 : " + message2.getUpdatedAt());
        System.out.println();

        // 모든 메세지 조회
        System.out.println("====== 전체 Message 조회  =====");
        List<Message> messageList = messageService.getAllMessages();
        System.out.println("총 Message 개수 : " + messageList.size());
        for (Message message : messageList) {
            System.out.println(message);
        }
        System.out.println();

        // 메세지 삭제
        System.out.println("====== Message 삭제 ======");
        messageService.deleteMessage(message3.getId());
        System.out.println("삭제된 Message 내용 : " + message3.getContent());
        System.out.println("-- 삭제 후 전체 Message 조회 --");
        List<Message> messageList1 = messageService.getAllMessages();
        System.out.println("현재 Message 개수 : " + messageList1.size());
        for (Message message : messageList1) {
            System.out.println(message);
        }
    }

    public static void channelCRUDTest(JCFChannelService channelService){

        System.out.println("---------------------------");
        System.out.println("Channel 4개 생성이 완료되었습니다.");
        System.out.println("---------------------------");
        System.out.println();

        Channel channel1 = channelService.createChannel("공지");
        Channel channel2 = channelService.createChannel("문의");
        Channel channel3 = channelService.createChannel("학습");
        Channel channel4 = channelService.createChannel("스터디");


        // 채널 단건 조회
        System.out.println("====== Channel 단건 조회  =====");
        channelService.getChannel(channel3.getId());
        System.out.println(channel3);
        System.out.println();

        // 채널 제목 수정
        System.out.println("====== channel2 이름 수정  =====");
        System.out.println("수정 전 이름 : " + channel2.getChannelname() + ", 생성시간 : " + channel2.getCreatedAt());
        channelService.updateChannel(channel2.getId(), "자유"); // 학습 -> 자유 로 이름 변경
        System.out.println("수정 후 이름 : " + channel2.getChannelname() + ", 수정시간 : " + channel2.getUpdatedAt());
        System.out.println();

        // 모든 채널 조회
        System.out.println("====== 전체 Channel 조회  =====");
        List<Channel> channelList = channelService.getAllChannels();
        System.out.println("총 Channel 개수 : " + channelList.size());
        for (Channel channel : channelList) {
            System.out.println(channel);
        }
        System.out.println();

        // 채널 삭제
        System.out.println("====== Channel 삭제 ======");
        channelService.deleteChannel(channel3.getId());
        System.out.println("삭제된 Channel 이름 : " + channel3.getChannelname());
        System.out.println("-- 삭제 후 전체 Channel 조회 --");
        List<Channel> ChannelList1 = channelService.getAllChannels();
        System.out.println("현재 Channel 개수 : " + ChannelList1.size());
        for (Channel channel : ChannelList1) {
            System.out.println(channel);
        }
    }
}
