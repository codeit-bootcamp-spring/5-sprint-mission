package com.sprint.mission.discodeit;


import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.repository.file.*;
import com.sprint.mission.discodeit.repository.jcf.*;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.basic.*;

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
        // userCRUDTest((BasicUserService)userServiceFile);
        // messageCRUDTest((BasicMessageService) messageServiceFile);
        channelCRUDTest((BasicChannelService) channelServiceFile);

    }

    public static void userCRUDTest(BasicUserService userService){

        // 생성
        System.out.println("---------------------------");
        System.out.println("User 5명 생성이 완료되었습니다.");
        System.out.println("---------------------------");

        List<User> createUsers = Arrays.asList(
                new User("Woody", "woody@gmail.com", "1234"),
                new User("Alice", "alice@gmail.com", "1234"),
                new User("Bob", "bob@gmail.com", "1234"),
                new User("Eve", "eve@gmail.com", "1234"),
                new User("Joy", "joy@gmail.com", "1234")
        );

        // userService에 저장
        List<User> users  = createUsers.stream()
                .map(user -> userService.createUser(user.getUsername(), user.getEmail(), user.getPassword()))
                .collect(Collectors.toList());

        for (User user : users) {
            System.out.println("유저 생성 : " + user.getUsername() + ", 이메일 : " + user.getEmail());
        }
        System.out.println();

        // 단건 조회
        Optional<User> findUser = userService.getUser(users.get(2).getId());
        findUser.ifPresent(user -> {
            System.out.println("찾은 사람 : " + user.getUsername() + ", 이메일 : "+ user.getEmail() + "\n");
        });


        // 사용자 이름 수정
        User updatedUser = users.get(0);
        System.out.println("수정 전 이름 : " + updatedUser.getUsername() + ", 생성시간 : " + updatedUser.getCreatedAt());
        updatedUser.update("Buzz", "buzz@gmail.com","1234");
        userService.updateUser(updatedUser.getId(), updatedUser); // Woody -> Buzz 으로 이름 변경
        System.out.println("수정 후 이름 : " + updatedUser.getUsername() + ", 수정시간 : " + updatedUser.getUpdatedAt());
        System.out.println();


        // 모든 사용자 조회
        System.out.println("====== 전체 유저 조회  =====");
        System.out.println("총 유저 수 : " + userService.getAllUsers().size());
        // System.out.println(userService.getAllUsers());
        for (User user : users) {
            System.out.println("유저 이름 : " + user.getUsername() + ", 이메일 : " + user.getEmail());
        }
        System.out.println();


        // 사용자 삭제
        System.out.println("====== 유저 삭제 ======");
        User deletedUser = users.get(1);
        userService.deleteUser(deletedUser.getId());
        System.out.println("삭제된 유저 이름 : " + deletedUser.getUsername());
        System.out.println();

        // 삭제 후 전체 유저 조회
        System.out.println("-- 삭제 후 전체 유저 조회 --");
        List<User> users1 = userService.getAllUsers();
        System.out.println("현재 유저 수 : " + users1.size());
        for (User user : users1) {
            System.out.println("유저 이름 : " + user.getUsername() + ", 이메일 : " + user.getEmail());
        }
        System.out.println();

        // 사용자 존재 여부 확인
        System.out.println("====== 유저 존재 여부 확인  ======");
        boolean result = userService.existsById(deletedUser.getId());
        System.out.println("'" + deletedUser.getUsername() + "' 의 존재 여부 확인 : " + result);
        User user3 = users.get(2);
        boolean result1 = userService.existsById(user3.getId());
        System.out.println("'" + user3.getUsername() + "' 의 존재 여부 확인 : " + result1);

    }


    public static void messageCRUDTest(BasicMessageService messageService){

        System.out.println("---------------------------");
        System.out.println(" ✅ 메세지 3건 생성이 완료되었습니다.");
        System.out.println("---------------------------");
        System.out.println();


        List<String> createMessages = Arrays.asList(
                "Hello Java World!", "Happy Birthday", "Good Morning!"
        );
        List<Message> messages = createMessages.stream()
                .map(content -> messageService.createMessage(content, UUID.randomUUID(), UUID.randomUUID()))
                .collect(Collectors.toList());

        for (Message message : messages) {
            System.out.println("메세지 생성 : " + message.getContent());
        }
        System.out.println();


        // 단건 조회
        Optional<Message> findMessage = messageService.getMessage(messages.get(0).getId());
        findMessage.ifPresent(message -> {
            System.out.println("찾은 메세지 : " + message.getContent() + "\n");
        });


        // 메세지 내용 수정
        Message updatedMessage = messages.get(1);
        System.out.println("수정 전 내용 : " + updatedMessage.getContent() + ", 생성시간 : " + updatedMessage.getCreatedAt());
        messageService.updateMessage(updatedMessage.getId(),"Merry Christmas"); // Happy Birthday -> Merry Christmas 으로 메세지 변경
        System.out.println("수정 후 내용 : " + updatedMessage.getContent() + ", 수정시간 : " + updatedMessage.getUpdatedAt());
        System.out.println();


        // 모든 메세지 조회
        System.out.println("====== 전체 메세지 조회  =====");
        List<Message> messages1 = messageService.getAllMessages();
        System.out.println("총 메세지 개수 : " + messages1.size());
        // System.out.println(messageService.getAllMessages());
        for (Message message : messages1) {
            System.out.println("메세지 : " + message.getContent() + ", 채널ID : " + message.getChannelId() + ", 글쓴이ID : " + message.getAuthorId());
        }
        System.out.println();


        // 메세지 삭제
        System.out.println("** 메세지가 삭제되었습니다 **");
        Message deletedMessage = messages.get(2);
        messageService.deleteMessage(deletedMessage.getId());
        System.out.println("✅ 삭제된 Message 내용 : " + deletedMessage.getContent() + "\n");

        System.out.println("-- 삭제 후 전체 메세지 조회 --");
        List<Message> messages2 = messageService.getAllMessages();
        System.out.println("현재 메세지 개수 : " + messages2.size());
        for (Message message : messages2) {
            System.out.println("메세지 : " + message.getContent() + ", 채널ID : " + message.getChannelId() + ", 글쓴이ID : " + message.getAuthorId());
        }
        System.out.println();

        // 메세지 존재 여부 확인
        System.out.println("====== 메세지 존재 여부 확인  ======");
        boolean result = messageService.existsById(deletedMessage.getId());
        System.out.println("'" + deletedMessage.getContent() + "' 의 존재 여부 확인 : " + result);
        Message message3 = messages.get(1);
        boolean result1 = messageService.existsById(message3.getId());
        System.out.println("'" + message3.getContent() + "' 의 존재 여부 확인 : " + result1);
    }

    public static void channelCRUDTest(BasicChannelService channelService){

        System.out.println("---------------------------");
        System.out.println("✅ 채널 4개 생성이 완료되었습니다.");
        System.out.println("---------------------------");
        System.out.println();

        List<Channel> createChannels = Arrays.asList(
                new Channel(ChannelType.PUBLIC, "공지", "공지 채널입니다"),
                new Channel(ChannelType.PUBLIC, "문의", "문의 채널입니다"),
                new Channel(ChannelType.PUBLIC, "학습", "학습 채널입니다"),
                new Channel(ChannelType.PRIVATE, "스터디", "스터디 채널입니다")
        );

        // channelService에 저장
        List<Channel> channels = createChannels.stream()
                .map(channel -> channelService.create(channel.getType(), channel.getChannelname(), channel.getDescription()))
                .collect(Collectors.toList());

        for (Channel channel : channels) {
            System.out.println("채널 생성 : " + channel.getChannelname() + ", 채널 설명 : " + channel.getDescription());
        }
        System.out.println();


        // 채널 단건 조회
        Optional<Channel> findChannel = channelService.find(channels.get(1).getId());
        findChannel.ifPresent(channel -> {
            System.out.println("찾은 채널 : " + channel.getChannelname() + ", 채널 설명 : " + channel.getDescription() + "\n");
        });

        // 모든 채널 조회
        System.out.println("====== 전체 채널 조회  =====");
        List<Channel> channels1 = channelService.findAll();
        System.out.println("총 채널 개수 : " + channels1.size());
        // System.out.println(channelService.findAll());
        for (Channel channel : channels1) {
            System.out.println("채널명 : " + channel.getChannelname() + ", 채널설명 : "+ channel.getDescription());
        }
        System.out.println();


        // 채널 삭제
        Channel deletedChannel = channels.get(2);
        System.out.println("** 채널 <" + deletedChannel.getChannelname() + "> 이 삭제되었습니다 **");
        channelService.delete(deletedChannel.getId());
        System.out.println("삭제된 Channel 이름 : " + deletedChannel.getChannelname() + "\n");

        // 채널 삭제 후 전체 채널 조회
        System.out.println("-- 삭제 후 전체 채널 조회 --");
        List<Channel> channels2 = channelService.findAll();
        System.out.println("현재 Channel 개수 : " + channels2.size());
        for (Channel channel : channels2) {
            System.out.println("채널명 : " + channel.getChannelname() + ", 채널설명 : "+ channel.getDescription());
        }

    }
}
