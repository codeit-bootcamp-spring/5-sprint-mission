package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFMessageRepository implements MessageRepository {
    private final List<Message> data;
    public  JCFMessageRepository(){
        data=new ArrayList<>();
    }


    @Override
    public Message save(Message message) {
        data.add(message);
        return message;

    }

    @Override
    public Optional<Message> findById(UUID messageId) {
        for(Message message:data){
            if(message.getId().equals(messageId)){
                return Optional.of(message);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public long count() {
        return data.size();
    }

    @Override
    public Message delete(UUID messageId) {
        Message target=new Message();
        for(Message message:data){
            if(message.getId().equals(messageId)){
                target=message;
                data.remove(message);
            }
        }
        return target;
    }

    @Override
    public boolean existsById(UUID messageId) {
        for(Message message:data){
            if(message.getId().equals(messageId)){
                return true;
            }
        }
        return false;
    }
}
