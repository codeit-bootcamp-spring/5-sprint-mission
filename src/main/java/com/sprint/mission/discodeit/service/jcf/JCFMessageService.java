package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data;

    public JCFMessageService() {
        this.data = new HashMap<>();
    }

    @Override
    public Message save(Message msg) {
        validate(msg);
        data.put(msg.getId(), msg);
        return msg;
    }

    @Override
    public Message findById(UUID id) {
        return Optional.ofNullable(data.get(id))
                .orElseThrow(() -> new NoSuchElementException("Message not found: " + id));
    }

    @Override
    public List<Message> findAll() {
        return List.copyOf(data.values());
    }

    @Override
    public Message update(UUID id, Message msgDto) {
        validate(msgDto);

        Message msg = Optional.ofNullable(data.get(id))
                .orElseThrow(() -> new NoSuchElementException("Message not found: " + id));

        msg.editContent(msgDto.getContent());

        return msg;
    }

    @Override
    public void delete(UUID id) {
        Optional.ofNullable(data.remove(id))
                .orElseThrow(() -> new NoSuchElementException("Message not found: " + id));
    }

    private void validate(Message msgDto) {
        if (msgDto == null) {
            throw new IllegalArgumentException("Message must not be null");
        }
        if (msgDto.getChannelId() == null) {
            throw new IllegalArgumentException("Message channel is required");
        }
        if (msgDto.getAuthorId() == null) {
            throw new IllegalArgumentException("Message author is required");
        }
        if (msgDto.getContent() == null || msgDto.getContent().isBlank()) {
            throw new IllegalArgumentException("Message content is required");
        }
    }
}
