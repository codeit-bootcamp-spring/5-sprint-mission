package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {

    final Map<UUID, Message> data = new HashMap<>();

    @Override
    public Message createMsg(String content, String channelId, String authorId) {
        if(content == null || authorId == null || content.isBlank() ||
                authorId.isBlank()) {
            throw new IllegalArgumentException("username or password ois null or blank");
        }
        Message message = new Message(content, channelId, authorId);
        data.put(message.getId(), message);
        return message;
    }

    @Override
    public Message find(UUID messageId) {
        if(!data.containsKey(messageId)){
            throw new NoSuchElementException("user not found");
        }
        return data.get(messageId);
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Message update(UUID messageId, String content, String channelId, String authorId) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("content is null or blank");
        }
        if (channelId == null || channelId.isBlank()) {
            throw new IllegalArgumentException("channel is null or blank");
        }
        if (authorId == null || authorId.isBlank()) {
            throw new IllegalArgumentException("author is null or blank");
        }

        Message existingMessage = data.get(messageId);
        if (existingMessage == null) {
            throw new NoSuchElementException("Message not found");
        }

        // 새 User 인스턴스를 생성 (기존 데이터 재활용)
        Message updatedMessage = new Message(content, channelId, authorId);
        data.put(messageId, updatedMessage);
        return updatedMessage;
    }

    @Override
    public Message delete(UUID messageId) {
        Message message = data.remove(messageId);
        if(message == null){
            throw new NoSuchElementException("message not found");
        }
        return message;
    }
}
