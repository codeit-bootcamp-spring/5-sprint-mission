package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.UUID;

public class FileMessageService implements MessageService {
    @Override
    public void createMessage(Message message) {

    }

    @Override
    public void updateMessage(Message message) {

    }

    @Override
    public void deleteMessage(Message message) {

    }

    @Override
    public Message searchByIndex(int i) {
        return null;
    }

    @Override
    public Message searchById(UUID id) {
        return null;
    }

    @Override
    public List<Message> searchByContent(String content) {
        return List.of();
    }

    @Override
    public List<Message> searchBySenderId(UUID id) {
        return List.of();
    }

    @Override
    public List<Message> getAllMessages() {
        return List.of();
    }
}
