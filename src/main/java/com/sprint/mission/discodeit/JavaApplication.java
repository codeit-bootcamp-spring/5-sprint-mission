package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.io.IOException;
import java.util.List;

public class JavaApplication {
    public static void main(String[] args) throws IOException {
        testUser();
        testFileUser();
        testChannel();
        testFileChannel();
        testMessage();
        testFileMessage();
    }

    public static void testUser() {
        System.out.println("--------------User--------------");
        UserService userService = new JCFUserService(new JCFUserRepository());

        System.out.println("1. 등록");
        User user1 = userService.createUser("홍길동", "1234", 10, "aaa@aaa.aa");
        User user2 = userService.createUser("박길동", "5678", 20, "bbb@bbb.bb");
        System.out.println("등록 완료: " + user1);
        System.out.println("등록 완료: " + user2);

        System.out.println("2. 단건 조회");
        System.out.println(userService.readUser(user1.getId()));

        System.out.println("3. 전체 조회");
        List<User> users = userService.readAllUsers();
        if (users.isEmpty()) {
            System.out.println("등록된 회원이 없습니다.");
        } else {
            for (User u : users) {
                System.out.println(u);
            }
        }

        System.out.println("4. 회원 수정");
        user1.setUsername("정길동");
        userService.updateUser(user1);

        System.out.println("5. 회원 삭제");
        userService.deleteUser(user1.getId());
    }

    private static void testFileUser() throws IOException {
        System.out.println("--------------FileUser--------------");
        UserService userService = new FileUserService(new FileUserRepository());

        System.out.println("1. 등록");
        User user1 = userService.createUser("홍길동", "1234", 10, "aaa@aaa.aa");
        User user2 = userService.createUser("박길동", "5678", 20, "bbb@bbb.bb");
        System.out.println("등록 완료: " + user1);
        System.out.println("등록 완료: " + user2);

        System.out.println("2. 단건 조회");
        userService.readUser(user1.getId());

        System.out.println("3. 전체 조회");
        List<User> users = userService.readAllUsers();
        if (users.isEmpty()) {
            System.out.println("등록된 회원이 없습니다.");
        } else {
            for (User u : users) {
                System.out.println(u);
            }
        }

        System.out.println("4. 회원 수정");
        user1.setUsername("정길동");
        userService.updateUser(user1);

        System.out.println("5. 회원 삭제");
        userService.deleteUser(user1.getId());

    }

    public static void testChannel() {
        System.out.println("--------------Channel--------------");
        ChannelService channelService = new JCFChannelService(new JCFChannelRepository());

        System.out.println("1. 등록");
        Channel channel1 = channelService.createChannel("취미", "취미 공유방", ChannelType.PUBLIC);
        Channel channel2 = channelService.createChannel("공부", "공부 기록방", ChannelType.PRIVATE);
        System.out.println("등록 완료: " + channel1);
        System.out.println("등록 완료: " + channel2);

        System.out.println("2. 단건 조회");
        System.out.println(channelService.readChannel(channel1.getId()));

        System.out.println("3. 전체 조회");
        List<Channel> channels = channelService.readAllChannels();
        if (channels.isEmpty()) {
            System.out.println("등록된 채널이 없습니다.");
        } else {
            for (Channel ch : channels) {
                System.out.println(ch);
            }
        }

        System.out.println("4. 체널 수정");
        channel1.setName("취미-스포츠");
        channelService.updateChannel(channel1);

        System.out.println("5. 채널 삭제");
        channelService.deleteChannel(channel1.getId());
    }

    private static void testFileChannel() throws IOException {
        System.out.println("--------------FileChannel--------------");
        ChannelService channelService = new FileChannelService(new FileChannelRepository());

        System.out.println("1. 등록");
        Channel channel1 = channelService.createChannel("취미", "취미 공유방", ChannelType.PUBLIC);
        Channel channel2 = channelService.createChannel("공부", "공부 기록방", ChannelType.PRIVATE);
        System.out.println("등록 완료: " + channel1);
        System.out.println("등록 완료: " + channel2);

        System.out.println("2. 단건 조회");
        channelService.readChannel(channel1.getId());

        System.out.println("3. 전체 조회");
        List<Channel> channels = channelService.readAllChannels();
        if (channels.isEmpty()) {
            System.out.println("등록된 채널이 없습니다.");
        } else {
            for (Channel ch : channels) {
                System.out.println(ch);
            }
        }

        System.out.println("4. 채널 수정");
        channel1.setDescription("취미-스포츠");
        channelService.updateChannel(channel1);

        System.out.println("5. 채널 삭제");
        channelService.deleteChannel(channel1.getId());

    }


    public static void testMessage() {
        System.out.println("--------------Message--------------");
        UserService userService = new JCFUserService(new JCFUserRepository());
        ChannelService channelService = new JCFChannelService(new JCFChannelRepository());
        MessageService messageService = new JCFMessageService(userService, channelService, new JCFMessageRepository());

        User user1 = userService.createUser("홍길동", "1234", 10, "aaa@aaa.aa");
        User user2 = userService.createUser("박길동", "5678", 20, "bbb@bbb.bb");

        Channel channel1 = channelService.createChannel("취미", "취미 공유방", ChannelType.PUBLIC);
        Channel channel2 = channelService.createChannel("공부", "공부 기록방", ChannelType.PRIVATE);


        System.out.println("1. 등록");
        Message message1 = messageService.createMessage(user1.getId(), channel1.getId(), "seo", "hi", "안녕");
        Message message2 = messageService.createMessage(user2.getId(), channel2.getId(), "yeon", "hello", "안녕하세요");
        System.out.println("등록 완료: " + message1);
        System.out.println("등록 완료: " + message2);

        System.out.println("2. 단건 조회");
        System.out.println(messageService.readMessage(message1.getId()));

        System.out.println("3. 전체 조회");
        List<Message> messages = messageService.readAllMessages();
        if (messages.isEmpty()) {
            System.out.println("등록된 메시지가 없습니다.");
        } else {
            for (Message m : messages) {
                System.out.println(m);
            }
        }

        System.out.println("4. 메시지 수정");
        message1.setName("seoyeon");
        messageService.updateMessage(message1);

        System.out.println("5. 메시지 삭제");
        messageService.deleteMessage(message1.getId());

    }

    private static void testFileMessage() throws IOException {
        System.out.println("--------------FileMessage--------------");
        UserService userService = new FileUserService(new FileUserRepository());
        ChannelService channelService = new FileChannelService(new FileChannelRepository());
        MessageService messageService = new FileMessageService(userService, channelService, new FileMessageRepository());

        User user1 = userService.createUser("홍길동", "1234", 10, "aaa@aaa.aa");
        User user2 = userService.createUser("박길동", "5678", 20, "bbb@bbb.bb");

        Channel channel1 = channelService.createChannel("취미", "취미 공유방", ChannelType.PUBLIC);
        Channel channel2 = channelService.createChannel("공부", "공부 기록방", ChannelType.PRIVATE);


        System.out.println("1. 등록");
        Message message1 = messageService.createMessage(user1.getId(), channel1.getId(), "seo", "hi", "안녕");
        Message message2 = messageService.createMessage(user2.getId(), channel2.getId(), "yeon", "hello", "안녕하세요");
        System.out.println("등록 완료: " + message1);
        System.out.println("등록 완료: " + message2);

        System.out.println("2. 단건 조회");
        messageService.readMessage(message1.getId());

        System.out.println("3. 전체 조회");
        List<Message> messages = messageService.readAllMessages();
        if (messages.isEmpty()) {
            System.out.println("등록된 회원이 없습니다.");
        } else {
            for (Message m : messages) {
                System.out.println(m);
            }
        }

        System.out.println("4. 메시지 수정");
        message1.setName("seoyeon");
        messageService.updateMessage(message1);

        System.out.println("5. 회원 삭제");
        messageService.deleteMessage(message1.getId());
    }
}
