package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data;

    public JCFMessageService() {
        this.data = new HashMap<>();
    }

    @Override
    public Message save(Message msg) {
        if (isNotValid(msg)) {
            throw new IllegalArgumentException("invalid message data");
        }

        data.put(msg.getId(), msg);
        return msg;
    }

    @Override
    public Message findById(UUID id) {
        Message msg = data.get(id);
        if (msg == null) {
            throw new IllegalArgumentException("message not found");
        }

        return msg;
    }

    @Override
    public List<Message> findAll() {
        return List.copyOf(data.values());
    }

    @Override
    public Message update(UUID id, Message msgDto) {
        Message msg = data.get(id);
        if (msg == null) {
            throw new IllegalArgumentException("message not found");
        }

        if (msgDto == null || msgDto.getContent() == null) {
            throw new IllegalArgumentException("invalid message data");
        }

        msg.editContent(msgDto.getContent());

        return msg;
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }

    private boolean isNotValid(Message msg) {
        return msg == null || msg.getChannelId() == null || msg.getAuthorId() == null
                || msg.getContent() == null || msg.getContent().isBlank();
    }

}
