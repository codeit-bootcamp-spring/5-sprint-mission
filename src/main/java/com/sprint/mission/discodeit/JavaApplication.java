package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.time.Instant;
import java.util.List;
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
        }

        for(Message M: message.getAllMessages()){
            System.out.println(M.toString());
        }
        for(Channel C: channel.getAllChannels()){
            System.out.println(C.toString());
            System.out.println("-------------------------------------------------------");
        }



        for(User U: user.getAllUsers()){
            user.updateUserUpdatedAt(U.getId(), Instant.now().toEpochMilli());
        }

        for(Message M: message.getAllMessages()){
            message.updateMessageUpdatedAt(M.getId(), Instant.now().toEpochMilli());
        }

        for(Channel C: channel.getAllChannels()){
            channel.updateChannelUpdatedAt(C.getId(), Instant.now().toEpochMilli());

        }

        for(User U: user.getAllUsers()){
            System.out.println("-----------------------수정 후--------------------------");
            System.out.println(U.toString());
        }

        for(Message M: message.getAllMessages()){

            System.out.println(M.toString());

        }
        for(Channel C: channel.getAllChannels()){

            System.out.println(C.toString());
            System.out.println("-------------------------------------------------------");
        }



        for(int i =0; i<user.getAllUsers().size();i++){
            if( user.getAllUsers().get(i).getId() == null ){
                continue;
            }
            user.deleteUser(user.getAllUsers().get(i).getId());

            if(user.getAllUsers() == null) {
                break;
            }
        }

        for(int i =0; i<channel.getAllChannels().size();i++){
            if( channel.getAllChannels().get(i).getId() == null ){
                continue;
            }
            channel.deleteChannel(channel.getAllChannels().get(i).getId());

            if(channel.getAllChannels() == null) {
                break;
            }
        }

        for(int i =0; i<message.getAllMessages().size();i++){
            if( message.getAllMessages().get(i).getId() == null ){
                continue;
            }
            message.deleteMessage(message.getAllMessages().get(i).getId());

            if(message.getAllMessages() == null) {
                break;
            }
        }

        System.out.println("-----------------------삭제 후--------------------------");
        System.out.println(user.getAllUsers());
        System.out.println(message.getAllMessages());
        System.out.println(channel.getAllChannels());






    }
}
