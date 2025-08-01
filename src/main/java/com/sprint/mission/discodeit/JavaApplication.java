package com.sprint.mission.discodeit;


import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.repository.file.*;
import com.sprint.mission.discodeit.repository.jcf.*;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.basic.*;
import com.sprint.mission.discodeit.service.jcf.*;

import java.util.*;
import java.util.stream.Collectors;


public class JavaApplication {
    public static void main(String[] args) {

        // 서비스 초기화
        UserRepository userRepositoryJCF = new JCFUserRepository();
        UserService userServiceJCF = new BasicUserService(userRepositoryJCF);
        UserRepository userRepositoryFile = new FileUserRepository();
        UserService userServiceFile = new BasicUserService(userRepositoryFile);

        MessageRepository messageRepositoryJCF = new JCFMessageRepository();
        MessageService messageServiceJCF = new BasicMessageService(messageRepositoryJCF);
        MessageRepository messageRepositoryFile = new FileMessageRepository();
        MessageService messageServiceFile = new BasicMessageService(messageRepositoryFile);

        ChannelRepository channelRepositoryJCF = new JCFChannelRepository();
        ChannelService channelServiceJCF = new BasicChannelService(channelRepositoryJCF);
        ChannelRepository channelRepositoryFile = new FileChannelRepository();
        ChannelService channelServiceFile = new BasicChannelService(channelRepositoryFile);


        // JCF 저장 방식
        // userCRUDTest((BasicUserService)userServiceJCF);
        // messageCRUDTest((BasicMessageService)messageServiceJCF);
        // channelCRUDTest((BasicChannelService)channelServiceJCF);


        // 파일 저장 방식
        // userCRUDTest((BasicUserService)userServiceJCF);
        // messageCRUDTest((BasicMessageService) messageServiceFile);
        channelCRUDTest((BasicChannelService) channelServiceFile);

    }

    public static void userCRUDTest(BasicUserService userService){

        // 생성
        System.out.println("---------------------------");
        System.out.println("User 5명 생성이 완료되었습니다.");
        System.out.println("---------------------------");

        List<String> usernames = Arrays.asList("Wooody", "Alice", "Bob", "Eve", "Malary");
        List<User> users = usernames.stream()
                .map(userService::createUser)
                .collect(Collectors.toList());

        for (User user : users) {
            System.out.println("유저 생성 : " + user);
        }
        System.out.println();

        // 단건 조회
        Optional<User> findUser = userService.getUser(users.get(2).getId());
        System.out.println("찾은 사람 : " + findUser + "\n");

        // 사용자 이름 수정
        User user1 = users.get(0);
        System.out.println("수정 전 이름 : " + user1.getUsername() + ", 생성시간 : " + user1.getCreatedAt());
        user1.update("Buzz");
        userService.updateUser(user1.getId(), user1); // Woody -> Buzz 으로 이름 변경
        System.out.println("수정 후 이름 : " + user1.getUsername() + ", 수정시간 : " + user1.getUpdatedAt());
        System.out.println();

        // 모든 사용자 조회
        System.out.println("====== 전체 유저 조회  =====");
        System.out.println("총 유저 수 : " + userService.getAllUsers().size());
        System.out.println(userService.getAllUsers());
        System.out.println();


        // 사용자 삭제
        System.out.println("====== 유저 삭제 ======");
        User user2 = users.get(1);
        userService.deleteUser(user2.getId());
        System.out.println("삭제된 유저 이름 : " + user2.getUsername());
        System.out.println();
        System.out.println("-- 삭제 후 전체 유저 조회 --");
        System.out.println("현재 유저 수 : " + users.size());
        for (User user : users) {
            System.out.println(user);
        }
        System.out.println();

        // 사용자 존재 여부 확인
        System.out.println("====== 유저 존재 여부 확인  ======");
        boolean result = userService.existsById(user2.getId());
        System.out.println("'" + user2.getUsername() + "' 의 존재 여부 확인 : " + result);
        User user3 = users.get(2);
        boolean result1 = userService.existsById(user3.getId());
        System.out.println("'" + user3.getUsername() + "' 의 존재 여부 확인 : " + result1);

    }


    public static void messageCRUDTest(BasicMessageService messageService){

        System.out.println("---------------------------");
        System.out.println(" ✅ 메세지 3건 생성이 완료되었습니다.");
        System.out.println("---------------------------");
        System.out.println();

        List<String> messagesContent = Arrays.asList("Hello Java World!", "Hello Java World!", "Good Morning!");
        List<Message> messages = messagesContent.stream()
                        .map(messageService::createMessage)
                        .collect(Collectors.toList());

        for (Message message : messages) {
            System.out.println("메세지 생성 : " + message.getContent());
        }
        System.out.println();


        // 단건 조회
        Message message1 = messages.get(0);
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
        System.out.println("총 Message 개수 : " + messageService.getAllMessages().size());
        System.out.println(messageService.getAllMessages());
        System.out.println();


        // 메세지 삭제
        System.out.println("** 메세지가 삭제되었습니다 **");
        Message message3 = messages.get(2);
        messageService.deleteMessage(message3.getId());
        System.out.println("✅ 삭제된 Message 내용 : " + message3.getContent() + "\n");

        System.out.println("-- 삭제 후 전체 메세지 조회 --");
        System.out.println("현재 메세지 개수 : " + messages.size());
        for (Message message : messages) {
            System.out.println(message);
        }
        System.out.println();

        // 메세지 존재 여부 확인
        System.out.println("====== 메세지 존재 여부 확인  ======");
        Message message2 = messages.get(1);
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

        List<String> channelName = Arrays.asList("공지", "문의", "학습", "스터디");
        List<Channel> channels = channelName.stream()
                .map(channelService::createChannel)
                .collect(Collectors.toList());
        for (Channel channel : channels) {
            System.out.println("채널 생성 : " + channel.getChannelname());
        }
        System.out.println();


        // 채널 단건 조회
        Channel channel2 = channels.get(1);
        Optional<Channel> findChannel = channelService.getChannel(channel2.getId());
        System.out.println("찾은 채널 : " + findChannel.get().getChannelname() + "\n");


        // 채널 제목 수정
        System.out.println("수정 전 채널명 : " + channel2.getChannelname() + ", 생성시간 : " + channel2.getCreatedAt());
        channelService.updateChannel(channel2.getId(), "자유"); // 학습 -> 자유 로 채널명 변경
        System.out.println("수정 후 채널명 : " + channel2.getChannelname() + ", 수정시간 : " + channel2.getUpdatedAt());
        System.out.println();

        // 모든 채널 조회
        System.out.println("====== 전체 채널 조회  =====");
        System.out.println("총 채널 개수 : " + channelService.getAllChannels().size());
        System.out.println(channelService.getAllChannels());
        System.out.println();

        // 채널 삭제
        Channel channel3 = channels.get(2);
        System.out.println("** 채널 <" + channel3.getChannelname() + "> 이 삭제되었습니다 **");
        channelService.deleteChannel(channel3.getId());
        System.out.println("삭제된 Channel 이름 : " + channel3.getChannelname() + "\n");
        System.out.println("-- 삭제 후 전체 전체 조회 --");
        System.out.println("현재 Channel 개수 : " + channelService.getAllChannels().size());
        System.out.println(channelService.getAllChannels());
    }
}
