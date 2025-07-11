package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.time.Instant;
import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args){
        JCFChannelService channel = new JCFChannelService();
        JCFMessageService message = new JCFMessageService();
        JCFUserService user = new JCFUserService();

        user.createUser(new User());
        message.createMessage(new Message());
        channel.createChannel(new Channel());
        System.out.println("시작");

        for(User U: user.getAllUsers()){
            System.out.println("-----------------------수정 전--------------------------");
            System.out.println(U.toString());

            System.out.println("-------------------------------------------------------");
        }

        for(Message M: message.getAllMessages()){
            System.out.println("-----------------------수정 전--------------------------");
            System.out.println(M.toString());
            System.out.println("-------------------------------------------------------");
        }
        for(Channel C: channel.getAllChannels()){
            System.out.println("-----------------------수정 전--------------------------");
            System.out.println(C.toString());
            System.out.println("-------------------------------------------------------");
        }


        User user1=new User();
        user1.updateUpdatedAt(Instant.now().toEpochMilli());
        for(User U: user.getAllUsers()){
            user.updateUser(U.getId(), user1);
        }

        Message message1 = new Message();
        message1.updateUpdatedAt(Instant.now().toEpochMilli());
        for(Message M: message.getAllMessages()){
            message.updateMessage(M.getId(), message1);
        }

        Channel channel1 = new Channel();
        channel1.updateUpdatedAt(Instant.now().toEpochMilli());
        for(Channel C: channel.getAllChannels()){
            channel.updateChannel(C.getId(), channel1);

        }

        for(User U: user.getAllUsers()){
            System.out.println("-----------------------수정 후--------------------------");
            System.out.println(U.toString());
            System.out.println("-------------------------------------------------------");
        }

        for(Message M: message.getAllMessages()){
            System.out.println("-----------------------수정 후--------------------------");
            System.out.println(M.toString());
            System.out.println("-------------------------------------------------------");
        }
        for(Channel C: channel.getAllChannels()){
            System.out.println("-----------------------수정 후--------------------------");
            System.out.println(C.toString());
            System.out.println("-------------------------------------------------------");
        }


//        for(User U: user.getAllUsers()){
//            System.out.println(U.getId());
//            user.deleteUser(U.getId());
//        }
//        for(Message M: message.getAllMessages()){
//            message.deleteMessage(M.getId());
//        }
//        for(Channel C: channel.getAllChannels()){
//            channel.deleteChannel(C.getId());
//        }


//        System.out.println("-----------------------삭제 후--------------------------");
//        System.out.println(user.getAllUsers());
//        System.out.println(message.getAllMessages());
//        System.out.println(channel.getAllChannels());






    }
}
