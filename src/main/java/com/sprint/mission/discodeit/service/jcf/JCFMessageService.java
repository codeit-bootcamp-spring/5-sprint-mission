package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> messageMap;

    public JCFMessageService() {
        messageMap = new HashMap<>();
    }

    // 메시지 추가
    @Override
    public Message create(String content, UUID userId, UUID channelId) {
        if (content == null || content.isBlank() || userId == null || channelId == null) {
            throw new IllegalArgumentException("Message info is invalid");
        }
        Message message = new Message(content, userId, channelId);
        messageMap.put(message.getId(), message);
        return message;
    }

    // 메시지 조회
    @Override
    public Message find(UUID messageId) {
        Message message = messageMap.get(messageId);
        if (message == null) {
            throw new NoSuchElementException("Message not found");
        }
        return message;
    }

    // 메시지 전체 조회
    @Override
    public List<Message> findAll() {
        return new ArrayList<>(messageMap.values());
    }

    // 메시지 수정
    @Override
    public Message update(UUID messageId, String content) {
        Message message = messageMap.get(messageId);
        if (message == null) {
            throw new NoSuchElementException("Message not found");
        }
        message.update(content);
        return message;
    }

    // 메시지 삭제
    @Override
    public void delete(UUID messageId) {
        messageMap.remove(messageId);
    }
}
