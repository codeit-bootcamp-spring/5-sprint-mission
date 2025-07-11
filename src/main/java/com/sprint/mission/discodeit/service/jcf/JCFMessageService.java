package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    final List<Message> data;

    public JCFMessageService() {
        data = new ArrayList<>();
    }

    @Override
    public Message create(Message message) {

        if (message != null) {
            data.add(message);
            return message;
        }

        return null;
    }

    @Override
    public List<Message> getAll() {
        return data;
    }

    @Override
    public Message get(UUID id) {
        return data.stream().filter(m -> m.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public Message update(UUID id, String text) {
        Message message = data.stream().filter(m -> m.getId().equals(id)).findFirst().orElse(null);

        if (message != null) {
            message.update(text);
            return message;
        }

        return null;
    }

    @Override
    public void delete(UUID id) {
        Message message = data.stream().filter(m -> m.getId().equals(id)).findFirst().orElse(null);

        if (message != null) {
            data.remove(message);
        }
    }
}
