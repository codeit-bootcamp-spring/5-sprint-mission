package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.UUID;

public class JCFMessageService implements MessageService {

    @Override
    public Message sendMessage(String message) {
        Message msg = new Message();

    }

    @Override
    public Message find(UUID id) {
        return null;
    }

    @Override
    public Message findAll() {
        return null;
    }

    @Override
    public Message update(UUID id, String message) {
        return null;
    }

    @Override
    public boolean delete(UUID id) {
        return false;
    }
}
