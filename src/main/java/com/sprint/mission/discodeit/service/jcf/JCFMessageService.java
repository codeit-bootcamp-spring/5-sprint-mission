package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> messages;

    public JCFMessageService() {
        messages = new HashMap<>();
    }

    @Override
    public Message create(String content, String userId, UUID channelId) {
        if (content == null || content.isEmpty()) {
            throw new IllegalArgumentException("내용이 입력되지 않았습니다.");
        }
        Message message = new Message(content, userId, channelId);
        messages.put(message.getId(), message);
        return message;
    }

    @Override
    public String getMessageById(UUID id) {
        for (Message message : messages.values()) {
            if (message.getId().equals(id)) {
                return message.getContent();
            }
        }
        throw new NoSuchElementException("해당 ID의 메시지를 찾을 수 없습니다.");
    }

    @Override
    public List<Message> getMessages() {
        return new ArrayList<>(messages.values());
    }

    @Override
    public List<Message> getMessagesByChannel(UUID channelId) {
        List<Message> list = new ArrayList<>();
       for (Message message : messages.values()) {
           if (message.getChannelId().equals(channelId)) {
               list.add(message);
           }
       }
       if (list.isEmpty()) {
           throw new NoSuchElementException("해당 채널의 메시지가 없습니다.");
       }
       return list;
    }

    @Override
    public List<Message> getMessagesByUser(String userId) {
        List<Message> list = new ArrayList<>();
        for (Message message : messages.values()) {
            if (message.getUserId().equals(userId)) {
                list.add(message);
            }
        }
        if (list.isEmpty()) {
            throw new NoSuchElementException("해당 유저의 메시지가 없습니다.");
        }
        return list;
    }

    @Override
    public Message update(UUID messageId, String content) {
        Message message = messages.get(messageId);
        if (message == null) {
            throw new NoSuchElementException("존재하지 않는 메세지입니다.");
        }
        if (content == null || content.isEmpty()){
            throw new IllegalArgumentException(("내용이 없습니다."));
        }
        message.setContent(content);
        return message;
    }

    @Override
    public void delete(UUID messageId) {
        Message message = messages.get(messageId);
        if (!messages.containsKey(messageId)) {
            throw new NoSuchElementException("삭제할 메시지가 없습니다.");
        }
        messages.remove(messageId);
    }
}
