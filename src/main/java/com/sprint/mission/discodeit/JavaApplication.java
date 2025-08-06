package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

import java.io.File;
import java.util.List;


public class JavaApplication {
    private static User setupUser(UserService userService) {
        return userService.create("userTest", "1234", "test");
    }

    private static Channel setupChannel(ChannelService channelService, User user) {
        return channelService.create(ChannelType.PUBLIC, "[공지 채널]", user.getId(), "공지 채널입니다.");
    }

    private static Message setupMessage(MessageService messageService, User user, Channel channel) {
        return messageService.create(user.getId(), channel.getId(), "공지사항");
    }

    private static void deleteAllFile() {
        String[] dirList = {"USER", "CHANNEL", "MESSAGE"};
        for (String d : dirList) {
            File dir = new File(d);
            if (dir.exists() && dir.isDirectory()) {
                File[] files = dir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isFile()) {
                            file.delete();
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        deleteAllFile();

        System.out.println("️==== setUp 시작 ====");
//        UserRepository userRepository = new JCFUserRepository();
//        ChannelRepository channelRepository = new JCFChannelRepository();
//        MessageRepository messageRepository = new JCFMessageRepository();
        UserRepository userRepository = new FileUserRepository();
        ChannelRepository channelRepository = new FileChannelRepository();
        MessageRepository messageRepository = new FileMessageRepository();

        UserService userService = new BasicUserService(userRepository);
        ChannelService channelService = new BasicChannelService(channelRepository, userService);
        MessageService messageService = new BasicMessageService(messageRepository, channelService, userService);

        User user = setupUser(userService);
        Channel channel = setupChannel(channelService, user);
        Message message = setupMessage(messageService, user, channel);
        System.out.println("️️==== setUp 완료 ====");

        System.out.println("\n-------- userTest 시작 --------");
        userCreateTest(userService);
        userFindTest(userService);
        userUpdateTest(userService, user);
        userDeleteTest(userService, user);

        System.out.println("\n-------- channelTest 시작 --------");
        channelCreateTest(channelService, user);
        channelFindTest(channelService, user);
        channelUpdateTest(channelService, userService, channel, user);
        channelDeleteTest(channelService, userService, user);

        System.out.println("\n-------- messageTest 시작 --------");
        messageCreateTest(messageService, channel, user);
        messageFindTest(messageService, channel, user);
        messageUpdateTest(messageService, userService, message, user);
        messageDeleteTest(messageService, userService, user, channel);

        deleteAllFile();

        System.out.println("\n==== 전체 Test 결과 ====\n- 사용자 수 : " + userService.findAll().size() +
                "명\n- 채널 수 : " + channelService.findAll().size() +
                "개\n- 메시지 수 : " + messageService.findAll().size() + "개");
    }

    static void userCreateTest(UserService userService) {
        User user1 = userService.create("userTest1", "userTest1@mail.net", "1234");
        System.out.println("사용자 생성 결과 : " + userService.find(user1.getId()).getId().equals(user1.getId()));

        try {
            userService.create("userTest1", "userTest1@mail.net", "1234");
        } catch (Exception e) {
            System.out.println("중복 id 생성 결과 : " + e.getMessage());
        }
    }

    static void userFindTest(UserService userService) {
        User user2 = userService.create("userTest2", "userTest2@mail.net", "1234");

        System.out.println("사용자 조회 결과 : " + userService.find(user2.getId()).getId().equals(user2.getId()));
        System.out.println("전체 사용자 조회 결과 : " + (userService.findAll().size() == 3));
        List<User> arr = userService.searchByUsernameOrEmail("");
        System.out.println("'user' 검색 결과 : " + arr.size() + "개");
        for (User u : arr) {
            System.out.println("- " + u.getUsername() + ", " + u.getEmail());
        }
    }

    static void userUpdateTest(UserService userService, User user) {
        userService.update(user.getId(), user.getId(), "updatedUserTest", "updatedUserTest@mail.net", "5678");
        System.out.println("사용자 정보 수정 결과 : " + userService.find(user.getId()).getUsername().equals("updatedUserTest"));

        User user3 = userService.create("userTest3", "userTest3@mail.net", "1234");

        try {
            userService.update(user.getId(), user3.getId(), "updatedUserTest", "updatedUserTest@mail.net", "5678");
        } catch (Exception e) {
            System.out.println("다른 사용자가 사용자 정보 수정 접근 : " + e.getMessage());
        }
    }

    static void userDeleteTest(UserService userService, User user) {
        User user4 = userService.create("userTest4", "userTest4@mail.net", "1234");
        try {
            userService.delete(user4.getId(), user.getId());
        } catch (Exception e) {
            System.out.println("다른 사용자가 계정 삭제 접근 : " + e.getMessage());
        }

        userService.delete(user4.getId(), user4.getId());
        try {
            userService.delete(user4.getId(), user4.getId());
        } catch (Exception e) {
            System.out.println("계정 삭제 후 반복 삭제 시도 : " + e.getMessage());
        }
    }

    private static void channelCreateTest(ChannelService channelService, User user) {
        Channel channel1 = channelService.create(ChannelType.PUBLIC, "[채널1]", user.getId(), "채널1 압나다.");
        System.out.println("채널 생성 결과 : " + channelService.find(channel1.getId()).getId().equals(channel1.getId()));
    }

    private static void channelFindTest(ChannelService channelService, User user) {
        Channel channel2 = channelService.create(ChannelType.PUBLIC, "[채널2]", user.getId(), "채널2 압나다.");

        System.out.println("채널 조회 결과 : " + channelService.find(channel2.getId()).getId().equals(channel2.getId()));
        System.out.println("전체 채널 조회 결과 : " + (channelService.findAll().size() == 3));
        List<Channel> arr = channelService.searchByName("공지");
        System.out.println("'공지' 검색 결과 : " + arr.size() + "개");
        for (Channel c : arr) {
            System.out.println("- " + c.getName());
        }
    }

    private static void channelUpdateTest(ChannelService channelService, UserService userService, Channel channel, User user) {
        channelService.update(channel.getId(), user.getId(), "updatedName", "수정되었습니다.");
        System.out.println("채널명 수정 결과 : " + channelService.find(channel.getId()).getName().equals("updatedName"));

        User user1 = userService.create("channelTest1", "1234", "test1");

        try {
            channelService.update(channel.getId(), user1.getId(), "updatedName", "수정되었습니다.");
        } catch (Exception e) {
            System.out.println("다른 사용자가 채널명 수정 접근 : " + e.getMessage());
        }
    }

    private static void channelDeleteTest(ChannelService channelService, UserService userService, User user) {
        User user1 = userService.create("channelTest2", "1234", "test4");

        Channel channel3 = channelService.create(ChannelType.PUBLIC, "[채널3]", user.getId(), "채널3 압나다.");

        try {
            channelService.delete(channel3.getId(), user1.getId());
        } catch (Exception e) {
            System.out.println("다른 사용자가 채널 삭제 접근 : " + e.getMessage());
        }

        channelService.delete(channel3.getId(), user.getId());

        try {
            channelService.delete(channel3.getId(), user.getId());
        } catch (Exception e) {
            System.out.println("채널 삭제 후 반복 삭제 시도: " + e.getMessage());
        }
    }

    private static void messageCreateTest(MessageService messageService, Channel channel, User user) {
        Message message1 = messageService.create(user.getId(), channel.getId(), "메시지1");
        System.out.println("메시지 생성 결과 : " + messageService.find(message1.getId()).getId().equals(message1.getId()));
    }

    private static void messageFindTest(MessageService messageService, Channel channel, User user) {
        Message message2 = messageService.create(user.getId(), channel.getId(), "메시지2");

        System.out.println("메시지 조회 결과 : " + messageService.find(message2.getId()).getId().equals(message2.getId()));
        System.out.println("전체 메시지 조회 결과 : " + (messageService.findAll().size() == 3));
        List<Message> arr = messageService.searchByContent("2");
        System.out.println("'2' 검색 결과 " + arr.size() + "개");
        for (Message m : arr) {
            System.out.println("- " + m.getContent());
        }
    }

    private static void messageUpdateTest(MessageService messageService, UserService userService, Message message, User user) {
        messageService.update(message.getId(), user.getId(), "updatedContent");
        System.out.println("메시지 내용 수정 결과 : " + messageService.find(message.getId()).getContent().equals("updatedContent"));

        User user1 = userService.create("messageTest1", "1234", "test1");

        try {
            messageService.update(message.getId(), user1.getId(), "updatedContent");
        } catch (Exception e) {
            System.out.println("다른 사용자가 메시지 내용 수정 접근 : " + e.getMessage());
        }
    }

    private static void messageDeleteTest(MessageService messageService, UserService userService, User user, Channel channel) {
        User user2 = userService.create("messageTest2", "1234", "test2");
        Message message4 = messageService.create(user.getId(), channel.getId(), "메시지4");

        try {
            messageService.delete(message4.getId(), user2.getId());
        } catch (Exception e) {
            System.out.println("다른 멤버가 메시지 삭제 접근 : " + e.getMessage());
        }

        messageService.delete(message4.getId(), user.getId());

        try {
            messageService.delete(message4.getId(), user.getId());
        } catch (Exception e) {
            System.out.println("메시지 삭제 후 반복 삭제 시도: " + e.getMessage());
        }
    }


}