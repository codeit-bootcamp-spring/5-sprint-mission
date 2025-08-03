package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.*;

public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> data;

    public JCFMessageRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public Message save(Message messageDto) {
        data.put(messageDto.getId(), messageDto);
        return messageDto;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Message> findAll() {
        return List.copyOf(data.values());
    }

    @Override
    public Message update(UUID id, Message messageDto) {
        Message msg = data.get(id);
        msg.editContent(messageDto.getContent());
        return msg;
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }
}
