package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    MessageRepository repo;
    JCFUserService jcfUserService;

    public JCFMessageService(){
        this.repo=new JCFMessageRepository();
        this.jcfUserService=new JCFUserService();

    }

    public Message createMessage(String content,UUID channelId,UUID authorId){
        Message message=new Message(content,channelId,authorId);
        repo.save(message);
        System.out.println("message 추가 성공");
        return  message;
    }

    public Message getMessageById(UUID messageId){
        if(repo.count()==0){
            return null;
        }
        for(Message message: repo.findAll()){
            if(message.getId().equals(messageId)){
                return message;
            }
        }
        return null;
    };

    public List<Message> getAllMessages(){
        if(repo.count()==0){
            return null;
        }
        return repo.findAll();
    }

    @Override
    public List<Message> getAllMessagesByChannelId(UUID channelId) {
        if(repo.count()==0){
            return null;
        }
        List<Message> messages=new ArrayList<>();
        for(Message message:repo.findAll()){
            if(message.getChannelId().equals(channelId)){
                messages.add(message);
            }
        }
        return messages;
    }

    @Override
    public List<Message> getAllMessagesByAuthorId(UUID authorId) {
        if(repo.count()==0){
            return null;
        }
        List<Message> messages=new ArrayList<>();
        for(Message message:repo.findAll()){
            if(message.getAuthorId().equals(authorId)){
                messages.add(message);
            }
        }
        return messages;
    }

//    @Override
//    public List<Message> getMessageByNick(String nick) {
//        if(data.size()==0){
//            return null;
//        }
//
//        List<Message> messages=new ArrayList<>();
//        for(Message message:data){
//            if(jcfUserService.getUserById(message.getAuthorId()).getNick().equals(nick) ){
//                messages.add(message);
//            }
//        }
//        return messages;
//    }

    @Override
    public Message updateMessageContent(UUID messageId, String content) {
        if(repo.count()==0){
            return null;
        }
        Message target=null;
        for(Message message:repo.findAll()){
            if(message.getId().equals(messageId)){
                message.updateContent(content);
                target=message;
            }
        }
        return target;
    }


    public Message deleteMessage(UUID messageId){
        Message target=null;
        if(repo.count()==0){
            return null;
        }
        for(Message message:repo.findAll()){
            if(message.getId().equals(messageId)){
                target=message;
                repo.delete(messageId);
            }
        }
        return target;

    };
}
