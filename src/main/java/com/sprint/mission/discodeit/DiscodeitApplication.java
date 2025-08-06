package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.config.AppConfig;
import com.sprint.mission.discodeit.dto.AddUserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {
    public static ApplicationContext appConfig;
    public static UserService userService;
    public static ChannelService channelService;
    public static MessageService messageService;

    public static void main(String[] args) {
        ApplicationContext appConfig = SpringApplication.run(DiscodeitApplication.class, args);

        userService = appConfig.getBean(UserService.class);
        channelService = appConfig.getBean(ChannelService.class);
        messageService = appConfig.getBean(MessageService.class);
        testAll();
	}

    public static void testAll(){
        try{
            JavaApplication.UserServiceTest.testAll();
            JavaApplication.MessageServiceTest.testAll();
            JavaApplication.ChannelServiceTest.testAll();
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

            Channel channel1 = channelService.addChannel("channel1", user1.getId());
            Channel channel2 = channelService.addChannel("channel2", user2.getId());

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

            Channel channel = channelService.addChannel("channel1", user1.getId());
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

            Channel channel1 = channelService.addChannel("channel1", user1.getId());

            List<Channel> before = channelService.getAllChannel();

            userService.deleteUser(user1.getId());
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
            Channel channel = channelService.addChannel("channel1", user.getId());
            Message message1 = messageService.addMessage("message1", user.getId());

            Message findMessage1 = messageService.getMessageById(message1.getId());
            boolean result1 = message1.equals(findMessage1);

            Message message2 = messageService.addMessage("message1", user.getId());

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
            Channel channel = channelService.addChannel("channel1", user.getId());
            Message message1 = messageService.addMessage("message1", user.getId());

            Message originMessage = messageService.getMessageById(message1.getId());
            Message updatedMessage = messageService.updateMessage(originMessage.getId(), "메시지 업데이트 내용");

            boolean result1 = originMessage.getId().equals(updatedMessage.getId());
            boolean result2 = updatedMessage.getContent().equals("메시지 업데이트 내용");

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
            Channel channel = channelService.addChannel("channel1", user.getId());
            Message message1 = messageService.addMessage("message1", user.getId());

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

            Channel channel1 = channelService.addChannel("channel1", user1.getId());

            userService.joinChannel(channel1.getId(), user2.getId());

            Channel channelById1 = channelService.getChannelById(channel1.getId());
            Set<UUID> usersId = channelById1.getUsersId();
            boolean result1 = usersId.contains(user2.getId());

            userService.exitChannel(channel1.getId(), user2.getId());

            Channel channelById2 = channelService.getChannelById(channel1.getId());
            Set<UUID> usersId1 = channelById2.getUsersId();
            boolean result2 = !usersId1.contains(user2.getId());

            if(result1 && result2){
                System.out.println("UserServiceTest: 채널참가 및 나가기 : O");
            }else{
                System.out.println("UserServiceTest: 채널참가 및 나가기: X");
            }
        }
    }

}
