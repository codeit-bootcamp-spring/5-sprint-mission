package com.sprint.mission.discodeit.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {
    final Map<UUID, Message> data = new HashMap<>();

    @Override
    public Message createMessage(String channelName, String nickName, String message) {
        Message msg = new Message(channelName, nickName, message);
        data.put(msg.getId(), msg);
        return msg;
    }

    @Override
    public Message find(UUID uuid) {
        return null;
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Message updateMessage(UUID uuid, String message) {
        Message msg = data.get(uuid);
        data.put(uuid,msg);
        if(msg != null){
            msg.updateMessage(message);
        }
        return msg;
    }

    @Override
    public Message deleteMessage(UUID uuid) {
        return data.remove(uuid);
    }

}
