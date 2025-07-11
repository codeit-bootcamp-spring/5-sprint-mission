package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFMessageService {
    private final List<Message> data;

    public JCFMessageService(){
        data=new ArrayList<>();
    }

    public void createMessage(Message message){
        data.add(message);
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

    public void deleteMessage(UUID messageId){
        if(data.size()==0){
            return ;
        }
        for(Message message1:data){
            if(message1.getId().equals(messageId)){
                data.remove(message1);
            }
        }
    };
}
