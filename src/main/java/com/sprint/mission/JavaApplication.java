package com.sprint.mission;

import com.sprint.mission.discodeit.config.AppConfig;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.Set;

public class JavaApplication {

    public static AppConfig appConfig = new AppConfig();
    public static UserService userService = appConfig.userService();
    public static ChannelService channelService = appConfig.channelService();
    public static MessageService messageService = appConfig.messageService();

    public static void main(String[] args) {
        UserServiceTest.testAll();
        MessageServiceTest.testAll();
        ChannelServiceTest.testAll();
    }

    public static class ChannelServiceTest{
        static User user1 = new User("test1","test1@naver.com", "1234", "010-1234-6545");
        static Channel channel1 = new Channel("channel1", user1);
        static Channel channel2 = new Channel("channel2", user1);

        public static void testAll(){
            addAndFind();
            update();
            delete();
        }

        public static void addAndFind(){
            channelService.deleteAll();

            channelService.add(channel1);
            channelService.add(channel2);

            Channel findChannel1 = channelService.findOne(channel1.getId());
            boolean result1 = channel1.equals(findChannel1);

            List<Channel> findAllChannel = channelService.findAll();

            boolean result2 = findAllChannel.contains(channel2);

            if(result1 && result2){
                System.out.println("ChannelServiceTest : 등록 및 조회(단건, 다건) : O");
            }else{
                System.out.println("ChannelServiceTest : 등록 및 조회(단건, 다건) : X");
            }
        }

        public static void update(){
            channelService.deleteAll();

            channelService.add(channel1);
            Channel originChannel = channelService.findOne(channel1.getId());
            Channel updateChannel = new Channel("update1", user1);
            channelService.update(originChannel.getId(), updateChannel);

            Channel updatedChannel = channelService.findOne(originChannel.getId());

            boolean result1 = originChannel.getId().equals(updatedChannel.getId());
            boolean result2 = updateChannel.getChannelName().equals(updatedChannel.getChannelName());

            if(result1 && result2){
                System.out.println("ChannelServiceTest: 채널 업데이트 : O");
            }else{
                System.out.println("ChannelServiceTest: 채널 업데이트 : X");
            }
        }

        public static void delete(){
            channelService.deleteAll();

            channelService.add(channel1);
            channelService.add(channel2);

            List<Channel> before = channelService.findAll();

            channelService.delete(channel1.getId());

            List<Channel> after = channelService.findAll();

            boolean result = before.size() - 1 == after.size();
            if(result){
                System.out.println("ChannelServiceTest : 단건 삭제: O");
            }else{
                System.out.println("ChannelServiceTest : 단건 삭제: X");
            }
        }
    }

    public static class MessageServiceTest{

        static User user1 = new User("test1","test1@naver.com", "1234", "010-1234-6545");
        static Channel channel1 = new Channel("channel1", user1);
        static Message message1 = new Message("message1", channel1, user1);
        static Message message2 = new Message("message2", channel1, user1);


        public static void testAll(){
            addAndFind();
            update();
            delete();
        }

        public static void addAndFind(){
            messageService.deleteAll();

            messageService.add(message1);

            Message findMessage1 = messageService.findOne(message1.getId());
            boolean result1 = message1.equals(findMessage1);

            messageService.add(message2);
            List<Message> findAllMessage = messageService.findAll();
            boolean result2 = findAllMessage.contains(message2);
            if(result1 && result2){
                System.out.println("MessageServiceTest: 등록 및 조회(단건, 다건) : O");
            }else{
                System.out.println("MessageServiceTest: 등록 및 조회(단건, 다건) : X");
            }
        }

        public static void update(){
            messageService.deleteAll();

            messageService.add(message1);
            Message originMessage = messageService.findOne(message1.getId());
            Message updateMessage = new Message("update1", channel1, user1);
            messageService.update(originMessage.getId(), updateMessage);

            Message updatedMessage = messageService.findOne(originMessage.getId());

            boolean result1 = originMessage.getId().equals(updatedMessage.getId());
            boolean result2 = updateMessage.getContent().equals(updatedMessage.getContent());

            if(result1 && result2){
                System.out.println("MessageServiceTest: 메시지 업데이트 : O");
            }else{
                System.out.println("MessageServiceTest: 메시지 업데이트 : X");
            }
        }

        public static void delete(){
            messageService.deleteAll();

            messageService.add(message1);
            messageService.add(message2);

            List<Message> before = messageService.findAll();

            messageService.delete(message1.getId());

            List<Message> after = messageService.findAll();

            boolean result = before.size() - 1 == after.size();
            if(result){
                System.out.println("MessageServiceTest : 단건 삭제: O");
            }else{
                System.out.println("MessageServiceTest : 단건 삭제: X");
            }
        }
    }

    public static class UserServiceTest{
        static User user1 = new User("user1", "user1@gmail.com", "1234", "010-1234-6545");
        static User user2 = new User("user2", "user2@gmail.com", "1234", "010-456-6545");

        public static void testAll(){
            addAndFind();
            update();
            delete();
            joinChannel();
        }

        public static void addAndFind(){
            userService.deleteAll();
            userService.add(user1);
            userService.add(user2);

            User findUser1 = userService.findOne(user1.getId());

            boolean result1 = user1.equals(findUser1);

            boolean result2 = userService.findAll().contains(user2);

            if(result1 && result2){
                System.out.println("UserServiceTest: 등록 및 조회(단건, 다건): O");
            }else{
                System.out.println("UserServiceTest: 등록 및 조회(단건, 다건): X");
            }
        }

        public static void update(){
            userService.deleteAll();

            userService.add(user1);
            User updateUser = new User("update1", "update@naver.com", "1234", "101123456789");
            userService.update(user1.getId(), updateUser);
            User updatedUser = userService.findOne(user1.getId());

            boolean result1 = user1.getId().equals(updatedUser.getId());
            boolean result2 = updateUser.getUserName().equals(updatedUser.getUserName());

            if(result1 && result2){
                System.out.println("UserServiceTest: 업데이트 : O");
            }else{
                System.out.println("UserServiceTest: 업데이트 : X");
            }
        }

        public static void delete(){
            userService.deleteAll();
            userService.add(user1);
            userService.add(user2);

            List<User> before = userService.findAll();

            userService.delete(user1.getId());

            List<User> after = userService.findAll();

            boolean result = before.size() - 1 == after.size();
            if(result){
                System.out.println("UserServiceTest: 삭제 : O");
            }else{
                System.out.println("UserServiceTest: 삭제 : O");
            }

        }

        public static void joinChannel(){
            userService.deleteAll();
            userService.add(user1);

            Channel channel = new Channel("testChannel1", user1);
            channelService.add(channel);

            userService.joinChannel(channel.getId(), user1.getId());

            Set<User> users = channel.getUsers();
            boolean result = users.contains(user1);
            if(result){
                System.out.println("UserServiceTest: 채널참가 : O");
            }else{
                System.out.println("UserServiceTest: 채널참가 : X");
            }
        }
    }



}