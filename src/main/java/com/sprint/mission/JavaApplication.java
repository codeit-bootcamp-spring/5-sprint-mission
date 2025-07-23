package com.sprint.mission;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
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
    public static void main(String[] args) {
        UserServiceTest.testAll();
        ChannelServiceTest.testAll();
        MessageServiceTest.testAll();
    }

    public static class UserServiceTest{
        public static UserService jcfUserService = JCFUserService.getInstance();

        public static void testAll(){
            System.out.println("\n**UserService Test**");
            testAddUser();
            testGetUsers();
            testGetUserById();
            testGetUserByUsername();
            testUpdateUser();
            testDeleteUser();
        }
        public static void testAddUser(){
            jcfUserService.deleteAll();

            User originUser = new User(
                    "홍길동",
                    "hong@gmail.com",
                    "1234",
                    "010-1234-6545"
            );
            jcfUserService.addUser(originUser);

            User user =  jcfUserService.getUserByUsername("홍길동");
            if(user.getId().equals(originUser.getId())){
                System.out.println("testAddUser O");
            }else{
                System.out.println("testAddUser X");
            }

        }

        public static void testGetUsers(){
            jcfUserService.deleteAll();

            User u1 = new User("홍길동", "hong@gmail.com", "1234", "010-1234-6545");
            User u2 = new User("이순신", "lee@gmail.com", "5678", "010-5678-1234");
            jcfUserService.addUser(u1);
            jcfUserService.addUser(u2);

            List<User> list = jcfUserService.getUsers();
            if (list.size() == 2) {
                System.out.println("testGetUsers O");
            } else {
                System.out.println("testGetUsers X");
            }

        }

        public static void testGetUserById(){
            jcfUserService.deleteAll();

            User originUser = new User(
                    "홍길동",
                    "hong@gmail.com",
                    "1234",
                    "010-1234-6545"
            );

            jcfUserService.addUser(originUser);
            User user = jcfUserService.getUserById(originUser.getId());

            if (user != null && user.getId().equals(originUser.getId())) {
                System.out.println("testGetUserById O");
            } else {
                System.out.println("testGetUserById X");
            }
        }

        public static void testGetUserByUsername(){
            jcfUserService.deleteAll();

            User originUser = new User(
                    "이순신",
                    "lee@gmail.com",
                    "5678",
                    "010-5678-1234"
            );
            jcfUserService.addUser(originUser);

            User user = jcfUserService.getUserByUsername("이순신");
            if (user != null && "이순신".equals(user.getUserName())) {
                System.out.println("testGetUserByUsername O");
            } else {
                System.out.println("testGetUserByUsername X");
            }
        }

        public static void testUpdateUser(){
            jcfUserService.deleteAll();

            User originUser = new User(
                    "홍길동",
                    "hong@gmail.com",
                    "1234",
                    "010-1234-6545"
            );
            jcfUserService.addUser(originUser);

            originUser.updateUserName("김철수");
            originUser.updateEmail("kim@gmail.com");
            originUser.updatePassword("abcd");
            originUser.updatePhoneNumber("010-0000-0000");

            jcfUserService.updateUser(originUser, originUser.getId());

            User user = jcfUserService.getUserById(originUser.getId());
            if ("김철수".equals(user.getUserName())
                    && "kim@gmail.com".equals(user.getEmail())
                    && "abcd".equals(user.getPassword())
                    && "010-0000-0000".equals(user.getPhoneNumber())) {
                System.out.println("testUpdateUser O");
            } else {
                System.out.println("testUpdateUser X");
            }
            
        }

        public static void testDeleteUser(){
            jcfUserService.deleteAll();

            User originUser = new User(
                    "홍길동",
                    "hong@gmail.com",
                    "1234",
                    "010-1234-6545"
            );
            jcfUserService.addUser(originUser);
            jcfUserService.deleteUser(originUser.getId());

            User user = jcfUserService.getUserById(originUser.getId());
            if (user == null) {
                System.out.println("testDeleteUser O");
            } else {
                System.out.println("testDeleteUser X");
            }
        }
    }

    public static class ChannelServiceTest {
        private static final ChannelService jcfChannelService = JCFChannelService.getInstance();

        public static void testAll() {
            System.out.println("\n**ChannelService Test**");
            testAddChannel();
            testGetChannels();
            testGetChannelById();
            testUpdateChannel();
            testDeleteChannel();
        }

        public static void testAddChannel() {
            jcfChannelService.deleteAll();

            User owner = new User("홍길동", "hong@gmail.com", "1234", "010-1234-6545");
            Channel original = new Channel("소통 채", ChannelType.CHATTING, owner, "소통 채 주제");
            jcfChannelService.addChannel(original);

            Channel found = jcfChannelService.getChannelById(original.getId());
            if (found != null && found.getId().equals(original.getId())) {
                System.out.println("testAddChannel O");
            } else {
                System.out.println("testAddChannel X");
            }
        }

        public static void testGetChannels() {
            jcfChannelService.deleteAll();

            User u1 = new User("홍길동", "hong@gmail.com", "1234", "010-1234-6545");
            Channel c1 = new Channel("소통 채널", ChannelType.CHATTING, u1, "소통 채널 주제");
            User u2 = new User("이순신", "lee@gmail.com", "5678", "010-5678-1234");
            Channel c2 = new Channel("mbc 채널", ChannelType.CHATTING, u2, "mbc 채널 주제");

            jcfChannelService.addChannel(c1);
            jcfChannelService.addChannel(c2);

            List<Channel> list = jcfChannelService.getChannels();
            if (list.size() == 2) {
                System.out.println("testGetChannels O");
            } else {
                System.out.println("testGetChannels X");
            }
        }

        public static void testGetChannelById() {
            jcfChannelService.deleteAll();

            User owner = new User("홍길동", "hong@gmail.com", "1234", "010-1234-6545");
            Channel original = new Channel("소통 채널", ChannelType.CHATTING, owner, "소통 채널 주제");
            jcfChannelService.addChannel(original);

            Channel found = jcfChannelService.getChannelById(original.getId());
            if (found != null && found.getId().equals(original.getId())) {
                System.out.println("testGetChannelById O");
            } else {
                System.out.println("testGetChannelById X");
            }
        }

        public static void testUpdateChannel() {
            jcfChannelService.deleteAll();

            User owner = new User("홍길동", "hong@gmail.com", "1234", "010-1234-6545");
            Channel original = new Channel("소통 채널", ChannelType.CHATTING, owner, "소통 채널 주제");
            jcfChannelService.addChannel(original);

            User newOwner = new User("김철수", "kim@gmail.com", "abcd", "010-0000-0000");
            Channel updated = new Channel("뉴스 채널", ChannelType.CHATTING, newOwner, "뉴스 채널 주제");
            jcfChannelService.updateChannel(updated, original.getId());

            Channel found = jcfChannelService.getChannelById(original.getId());
            if (found != null
                    && "뉴스 채널".equals(found.getChannelName())
                    && found.getOwnerUser().getId().equals(newOwner.getId())
                    && "뉴스 채널 주제".equals(found.getTopic())
                    && found.getUpdatedAt() != null) {
                System.out.println("testUpdateChannel O");
            } else {
                System.out.println("testUpdateChannel X");
            }
        }

        public static void testDeleteChannel() {
            jcfChannelService.deleteAll();

            User owner = new User("홍길동", "hong@gmail.com", "1234", "010-1234-6545");
            Channel original = new Channel("소통 채널", ChannelType.CHATTING, owner, "소통 채널 주제");
            jcfChannelService.addChannel(original);
            jcfChannelService.deleteChannel(original.getId());

            Channel found = jcfChannelService.getChannelById(original.getId());
            if (found == null) {
                System.out.println("testDeleteChannel O");
            } else {
                System.out.println("testDeleteChannel X");
            }
        }
    }

    public static class MessageServiceTest {
        private static final MessageService jcfMessageService = JCFMessageService.getInstance();

        public static void testAll() {
            System.out.println("\n**MessageService Test**");
            testAddMessage();
            testGetMessages();
            testGetMessageById();
            testUpdateMessage();
            testDeleteMessage();
        }

        public static void testAddMessage() {
            jcfMessageService.deleteAll();

            User user = new User("user", "writer@example.com", "pw1234", "010-1111-2222");
            Channel channel = new Channel("일반", ChannelType.CHATTING, user, "일반 채널");
            Message original = new Message("첫 번째 메시지",  channel, user);
            jcfMessageService.addMessage(original);

            Message found = jcfMessageService.getMessageById(original.getId());
            if (found != null && found.getId().equals(original.getId())) {
                System.out.println("testAddMessage O");
            } else {
                System.out.println("testAddMessage X");
            }
        }

        public static void testGetMessages() {
            jcfMessageService.deleteAll();

            User user1 = new User("사용자1", "user1@example.com", "pass1", "010-2222-3333");
            Channel channel = new Channel("테스트", ChannelType.CHATTING, user1, "테스트 채널 주제");
            Message m1 = new Message("메시지 하나", channel, user1);
            Message m2 = new Message("메시지 둘", channel, user1);

            jcfMessageService.addMessage(m1);
            jcfMessageService.addMessage(m2);

            List<Message> list = jcfMessageService.getMessages();
            if (list.size() == 2) {
                System.out.println("testGetMessages O");
            } else {
                System.out.println("testGetMessages X");
            }
        }

        public static void testGetMessageById() {
            jcfMessageService.deleteAll();

            User user = new User("사용자조회", "lookup@example.com", "pw0000", "010-4444-5555");
            Channel channel = new Channel("조회", ChannelType.CHATTING, user, "조회 채널 주");
            Message original = new Message("조회할 메시지",  channel, user);
            jcfMessageService.addMessage(original);

            Message found = jcfMessageService.getMessageById(original.getId());
            if (found != null && found.getId().equals(original.getId())) {
                System.out.println("testGetMessageById O");
            } else {
                System.out.println("testGetMessageById X");
            }
        }

        public static void testUpdateMessage() {
            jcfMessageService.deleteAll();

            User user = new User("업데이트", "update@example.com", "pw1111", "010-6666-7777");
            Channel channel = new Channel("업데이트", ChannelType.CHATTING, user, "업데이트 channel");
            Message original = new Message("변경 전 내용", channel, user);
            jcfMessageService.addMessage(original);

            Message updated = new Message("변경 후 내용", channel, user);
            jcfMessageService.updateMessage(updated, original.getId());

            Message found = jcfMessageService.getMessageById(original.getId());
            if (found != null
                    && "변경 후 내용".equals(found.getContent())
                    && found.getUpdatedAt() != null) {
                System.out.println("testUpdateMessage O");
            } else {
                System.out.println("testUpdateMessage X");
            }
        }

        public static void testDeleteMessage() {
            jcfMessageService.deleteAll();

            User user = new User("삭제테스트", "del@example.com", "pw2222", "010-8888-9999");
            Channel channel = new Channel("삭제", ChannelType.CHATTING, user, "삭제 channel");
            Message original = new Message("삭제할 메시지", channel, user);
            jcfMessageService.addMessage(original);

            jcfMessageService.deleteMessage(original.getId());

            Message found = jcfMessageService.getMessageById(original.getId());
            if (found == null) {
                System.out.println("testDeleteMessage O");
            } else {
                System.out.println("testDeleteMessage X");
            }
        }
    }


}