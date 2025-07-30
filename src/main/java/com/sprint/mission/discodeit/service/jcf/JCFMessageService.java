package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private final List<Message> messageList;

    public JCFMessageService() {
        messageList = new ArrayList<>();
    }

    // 메시지 추가
    @Override
    public Message create(String content, UUID userId, UUID channelId) {
        if (content == null || content.isBlank() || userId == null || channelId == null) {
            return null;
        }
        Message message = new Message(content, userId, channelId);
        messageList.add(message);
        return message;
    }

    // 메시지 조회
    @Override
    public Message find(UUID messageId) {
        for (Message message : messageList) {
            if (message.getId().equals(messageId)) {
                return message;
            }
        }
        return null;
    }

    // 메시지 전체 조회
    @Override
    public List<Message> findAll() {
        return messageList;
    }

    // 메시지 수정
    @Override
    public Message update(UUID messageId, String content) {
        for (Message message : messageList) {
            if (message.getId().equals(messageId)) {
                message.update(content);
                return message;
            }
        }
        return null;
    }

    // 메시지 삭제
    @Override
    public boolean delete(UUID messageId) {
        for (int i = 0; i < messageList.size(); i++) {
            Message message = messageList.get(i);
            if (message.getId().equals(messageId)) {
                messageList.remove(i);
                return true;
            }
        }
        return false;
    }
}
