package com.sprint.mission.discodeit.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private final List<Message> messages = new ArrayList<>();

    @Override
    public Message register(Message message) {
        if (message.getUserId() == null || message.getChannelId() == null || message.getContent() == null || message.getContent().isBlank()) {
            System.out.println("메시지 등록 실패!");
            return null;
        }
        messages.add(message);
        System.out.println("메시지 : " + message.getContent() + " 등록 성공");
        return message;
    }

    @Override
    public Message findById(UUID id) {
        for (Message message : messages) {
            if (message.getId().equals(id)) {
                return message;
            }
        }
        return null;
    }

    @Override
    public List<Message> findAll() {
        return List.copyOf(messages);
    }

    @Override
    public Message update(UUID id, String newContent) {
        if (newContent != null && !newContent.isBlank()) {
            for (Message message : messages) {
                if (message.getId().equals(id)) {
                    message.setContent(newContent);
                    message.setUpdateAt(System.currentTimeMillis());
                    return message;
                }
            }
        }
        return null;
    }

    @Override
    public Message delete(UUID id) {
        for (Message message : messages) {
            if (message.getId().equals(id)) {
                messages.remove(message);
                return message;
            }
        }
        return null;
    }
}
