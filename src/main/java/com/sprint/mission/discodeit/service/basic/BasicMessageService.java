package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.UUID;

public class BasicMessageService implements MessageService {
    @Override
    public void create(Message message) {

    }

    @Override
    public void update(Message message) {

    }

    @Override
    public void delete(Message message) {

    }

    @Override
    public void deleteAll() {

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
    public List<Message> searchAll() {
        return List.of();
    }
}
