package com.sprint.mission;

import com.sprint.mission.discodeit.config.AppConfig;
import com.sprint.mission.discodeit.dto.AddUserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
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
        try{
            UserServiceTest.testAll();
            MessageServiceTest.testAll();
            ChannelServiceTest.testAll();
        } catch(Exception e){
            GlobalExceptionHandler.handleException(e);
        }

    }

    public static class ChannelServiceTest{
        public static void testAll(){
            addAndFind();
            update();
            delete();
        }

        public static void addAndFind(){
            channelService.deleteAllChannel();
            userService.deleteAllUser();
            messageService.deleteAllMessage();

            User user1 = userService.addUser(new AddUserDto("username1", "<EMAIL>", "1234", "010-1234-5678"));
            User user2 = userService.addUser(new AddUserDto("username2", "<EMAIL>", "1234", "010-1234-5678"));

            Channel channel1 = channelService.addChannel("channel1", user1);
            Channel channel2 = channelService.addChannel("channel2", user2);

            Channel findChannel1 = channelService.getChannelById(channel1.getId());
            boolean result1 = channel1.equals(findChannel1);

            List<Channel> findAllChannel = channelService.getAllChannel();

            boolean result2 = findAllChannel.contains(channel2);

            if(result1 && result2){
                System.out.println("ChannelServiceTest : 등록 및 조회(단건, 다건) : O");
            }else{
                System.out.println("ChannelServiceTest : 등록 및 조회(단건, 다건) : X");
            }
        }

        public static void update(){
            channelService.deleteAllChannel();
            userService.deleteAllUser();
            messageService.deleteAllMessage();

            User user1 = userService.addUser(new AddUserDto("username1", "<EMAIL>", "1234", "010-1234-5678"));

            Channel channel = channelService.addChannel("channel1", user1);
            Channel originChannel = channelService.getChannelById(channel.getId());

            channelService.updateChannel(originChannel.getId(), "update1");

            Channel updatedChannel = channelService.getChannelById(originChannel.getId());

            boolean result1 = originChannel.getId().equals(updatedChannel.getId());
            boolean result2 = updatedChannel.getChannelName().equals("update1");

            if(result1 && result2){
                System.out.println("ChannelServiceTest: 채널 업데이트 : O");
            }else{
                System.out.println("ChannelServiceTest: 채널 업데이트 : X");
            }
        }

        public static void delete(){
            channelService.deleteAllChannel();
            userService.deleteAllUser();
            messageService.deleteAllMessage();

            User user1 = userService.addUser(new AddUserDto("username1", "<EMAIL>", "1234", "010-1234-5678"));

            Channel channel1 = channelService.addChannel("channel1", user1);

            List<Channel> before = channelService.getAllChannel();

            channelService.deleteChannel(channel1.getId());

            List<Channel> after = channelService.getAllChannel();

            boolean result = before.size() - 1 == after.size();
            if(result){
                System.out.println("ChannelServiceTest : 단건 삭제: O");
            }else{
                System.out.println("ChannelServiceTest : 단건 삭제: X");
            }
        }
    }

    public static class MessageServiceTest{


        public static void testAll(){
            addAndFind();
            update();
            delete();
        }

        public static void addAndFind(){
            messageService.deleteAllMessage();
            channelService.deleteAllChannel();
            userService.deleteAllUser();

            User user = userService.addUser(new AddUserDto("username1", "<EMAIL>", "1234", "010-1234-5678"));
            Channel channel = channelService.addChannel("channel1", user);
            Message message1 = messageService.addMessage("message1", channel, user);

            Message findMessage1 = messageService.getMessageById(message1.getId());
            boolean result1 = message1.equals(findMessage1);

            Message message2 = messageService.addMessage("message1", channel, user);

            List<Message> findAllMessage = messageService.getAllMessage();
            boolean result2 = findAllMessage.contains(message2);

            if(result1 && result2){
                System.out.println("MessageServiceTest: 등록 및 조회(단건, 다건) : O");
            }else{
                System.out.println("MessageServiceTest: 등록 및 조회(단건, 다건) : X");
            }
        }

        public static void update(){
            messageService.deleteAllMessage();
            channelService.deleteAllChannel();
            userService.deleteAllUser();

            User user = userService.addUser(new AddUserDto("username1", "<EMAIL>", "1234", "010-1234-5678"));
            Channel channel = channelService.addChannel("channel1", user);
            Message message1 = messageService.addMessage("message1", channel, user);

            Message originMessage = messageService.getMessageById(message1.getId());
            Message updatedMessage = messageService.updateMessage(originMessage.getId(), "메시지 업데이트 내용");

            boolean result1 = originMessage.getId().equals(updatedMessage.getId());
            boolean result2 = originMessage.getContent().equals(updatedMessage.getContent());

            if(result1 && result2){
                System.out.println("MessageServiceTest: 메시지 업데이트 : O");
            }else{
                System.out.println("MessageServiceTest: 메시지 업데이트 : X");
            }
        }

        public static void delete(){
            messageService.deleteAllMessage();
            channelService.deleteAllChannel();
            userService.deleteAllUser();

            User user = userService.addUser(new AddUserDto("username1", "<EMAIL>", "1234", "010-1234-5678"));
            Channel channel = channelService.addChannel("channel1", user);
            Message message1 = messageService.addMessage("message1", channel, user);

            List<Message> before = messageService.getAllMessage();

            messageService.deleteMessage(message1.getId());

            List<Message> after = messageService.getAllMessage();

            boolean result = before.size() - 1 == after.size();
            if(result){
                System.out.println("MessageServiceTest : 단건 삭제: O");
            }else{
                System.out.println("MessageServiceTest : 단건 삭제: X");
            }
        }
    }

    public static class UserServiceTest{
        public static void testAll(){
            addAndFind();
            update();
            delete();
            joinAndExitChannel();
        }

        public static void addAndFind(){
            userService.deleteAllUser();
            messageService.deleteAllMessage();
            channelService.deleteAllChannel();

            User user1 = userService.addUser(new AddUserDto("username1", "<EMAIL>", "1234", "010-1234-5678"));
            User user2 = userService.addUser(new AddUserDto("username2", "<EMAIL>", "1234", "010-1234-5678"));

            User findUser1 = userService.getUserById(user1.getId());

            boolean result1 = user1.equals(findUser1);

            boolean result2 = userService.getAllUser().contains(user2);

            if(result1 && result2){
                System.out.println("UserServiceTest: 등록 및 조회(단건, 다건): O");
            }else{
                System.out.println("UserServiceTest: 등록 및 조회(단건, 다건): X");
            }
        }

        public static void update(){
            userService.deleteAllUser();
            messageService.deleteAllMessage();
            channelService.deleteAllChannel();

            User user1 = userService.addUser(new AddUserDto("username1", "<EMAIL>", "1234", "010-1234-5678"));

            User updatedUser = userService.updateUser(user1.getId(), new AddUserDto("update1", "<EMAIL>", "1234", "010-1234-5678"));

            boolean result1 = user1.getId().equals(updatedUser.getId());
            boolean result2 = updatedUser.getUserName().equals("update1");

            if(result1 && result2){
                System.out.println("UserServiceTest: 업데이트 : O");
            }else{
                System.out.println("UserServiceTest: 업데이트 : X");
            }
        }

        public static void delete(){
            userService.deleteAllUser();
            messageService.deleteAllMessage();
            channelService.deleteAllChannel();

            User user1 = userService.addUser(new AddUserDto("username1", "<EMAIL>", "1234", "010-1234-5678"));

            List<User> before = userService.getAllUser();

            userService.deleteUser(user1.getId());

            List<User> after = userService.getAllUser();

            boolean result = before.size() - 1 == after.size();
            if(result){
                System.out.println("UserServiceTest: 삭제 : O");
            }else{
                System.out.println("UserServiceTest: 삭제 : X");
            }

        }

        public static void joinAndExitChannel(){
            userService.deleteAllUser();
            messageService.deleteAllMessage();
            channelService.deleteAllChannel();

            User user1 = userService.addUser(new AddUserDto("username1", "<EMAIL>", "1234", "010-1234-5678"));
            User user2 = userService.addUser(new AddUserDto("username1", "<EMAIL>", "1234", "010-1234-5678"));

            Channel channel1 = channelService.addChannel("channel1", user1);

            userService.joinChannel(channel1.getId(), user2);

            Channel channelById1 = channelService.getChannelById(channel1.getId());
            Set<User> usersOfChannel1 = channelById1.getUsers();
            boolean result1 = usersOfChannel1.contains(user2);

            userService.exitChannel(channel1.getId(), user2);

            Channel channelById2 = channelService.getChannelById(channel1.getId());
            Set<User> usersOfChannel2 = channelById2.getUsers();
            boolean result2 = !usersOfChannel2.contains(user2);

            if(result1 && result2){
                System.out.println("UserServiceTest: 채널참가 : O");
            }else{
                System.out.println("UserServiceTest: 채널참가 : X");
            }
        }
    }



}