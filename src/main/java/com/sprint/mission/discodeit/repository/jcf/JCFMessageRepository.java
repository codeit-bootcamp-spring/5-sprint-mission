package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class JCFMessageRepository implements com.sprint.mission.discodeit.repository.MessageRepository {
    private final Map<UUID, Message> data = new HashMap<>();

    public JCFMessageRepository() {}

    @Override
    public Optional<Message> save(Message message) {
        if(message == null){
            return Optional.empty();
        }

        data.put(message.getId(), message);
        return Optional.of(message);
    }

    @Override
    public Optional<Message> findById(UUID messageId) {
        if(data.containsKey(messageId)){
            return Optional.of(data.get(messageId));
        }
        return Optional.empty();
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void delete(UUID messageId) {
        data.remove(messageId);
    }

    @Override
    public void deleteAll() {
        data.clear();
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        List<Message> resultList = new ArrayList<>();

        for(Message message : data.values()){
            if(message.getChannelId().equals(channelId)){
                resultList.add(message);
            }
        }

        return resultList;
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        data.entrySet().removeIf(entry ->
                entry.getValue().getChannelId().equals(channelId)
        );
    }
}
