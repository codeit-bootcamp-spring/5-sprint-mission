package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {
    final Map<UUID, Message> data= new HashMap<>();

    @Override
    public Message createMessage(UUID userId, UUID channelId, String content) {
        Message message = new Message(userId, channelId,content);
        data.put(message.getId(),message);
        return message;
    }

    @Override
    public Message readByIdMessage(UUID message) {
        return data.entrySet().stream()
                .filter(entry->entry.getKey().equals(message))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void readAllMessage() {
        data.entrySet().stream()
                .forEach(entry->
                        System.out.println(entry.getKey()+" "+entry.getValue()));
    }

    @Override
    public void updateMessage(UUID messageUUID, String content) {
        for(Map.Entry<UUID,Message>entry:data.entrySet()){
            UUID messageId = entry.getKey();
            Message message = entry.getValue();
            if(messageId.equals(messageUUID)){
                message.update(content);
                System.out.println("수정 성공하였습니다.");
                return;
            }
        }
        System.out.println("수정 실패하였습니다.");
    }

    @Override
    public void deleteByIdMessage(UUID message) {
        for(Map.Entry<UUID,Message>entry:data.entrySet()){
            UUID messageID = entry.getKey();
            if(messageID.equals(message)){
                data.remove(messageID);
                System.out.println("삭제 성공하였습니다.");
                return;
            }
        }
        System.out.println("삭제 실패하였습니다.");
    }
}
