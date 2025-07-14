package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.messageService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFMessageService implements messageService {
    private final List<Message> data;

    public JCFMessageService(){
        data=new ArrayList<>();
    }

    public void createMessage(Message message){
        data.add(message);
        System.out.println("추가 성공");
    }

    public Message getMessageById(UUID messageId){
        if(data.size()==0){
            return null;
        }
        for(Message message:data){
            if(message.getId().equals(messageId)){
                return message;
            }
        }
        return null;
    };

    public List<Message> getAllMessages(){
        if(data.size()==0){
            return null;
        }
        return data;
    };

    public void updateMessage(UUID messageId, Message message){
        if(data.size()==0){
            return ;
        }
        for(Message message1:data){
            if(message1.getId().equals(messageId)){
                message1.updateUpdatedAt(message.getUpdatedAt());
                message1.updateCreatedAt(message.getCreatedAt());
            }
        }
    };

    public void updateMessageUpdatedAt(UUID messageId, long updatedAt){
        if(data.size()==0){
            return ;
        }
        for(Message message1:data){
            if(message1.getId().equals(messageId)){
                message1.updateUpdatedAt(updatedAt);
            }
        }
    };



    public void deleteMessage(UUID messageId){
        if(data.size()==0){
            return ;
        }

        data.removeIf(message1 -> message1.getId().equals(messageId));
        System.out.println("삭제 성공");
//        List<Message> toRemove = new ArrayList<>();
//        for (Message message1 : data) {
//            if (message1.getId().equals(messageId)) {
//                toRemove.add(message1);
//            }
//        }
        //System.out.println(toRemove);
        //data.removeAll(toRemove);
    };
}
