package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.*;

public class JCFMessageRepository implements MessageRepository {
    final Map<UUID, Message> data;
    public JCFMessageRepository() {
        data = new HashMap<>();
    }
    @Override
    public Message save(Message message) {
        data.put(message.getId(), message);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public long count() {
        return data.size();
    }

    @Override
    public boolean delete(UUID id) {
        return  data.remove(id) != null;
    }

    @Override
    public boolean existsById(UUID id) {
        return data.containsKey(id);
    }

    @Override
    public boolean update(UUID messageUUID, String content) {
        Message message = data.get(messageUUID);
        if(message.getContent().equals(content)){
            System.out.println("수정 전과 일치합니다.");
            return false;
        }

        message.update(content);
        return true;
    }

}
