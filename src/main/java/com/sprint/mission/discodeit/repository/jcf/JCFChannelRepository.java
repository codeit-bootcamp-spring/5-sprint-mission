package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.*;

public class JCFChannelRepository implements ChannelRepository {

    private final Map<UUID, Channel> data;

    public JCFChannelRepository() {
        data = new HashMap<>();
    }

    @Override
    public Channel save(Channel channel) {
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        if(data.containsKey(id)) {
            return Optional.of(data.get(id));
        }
        return Optional.empty();
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel update(UUID id, String content) {
       Channel channel = data.get(id);

       if(!data.containsKey(id)) {
           throw new NoSuchElementException("Channel with id " + id + " does not exist");
       }
       channel.update(content);
       return channel;
    }

    @Override
    public Channel delete(UUID id) {
        if(!data.containsKey(id)) {
            throw new NoSuchElementException("Channel with id " + id + " does not exist");
        }
        return data.remove(id);
    }

    @Override
    public boolean existById(UUID id) {
        return data.containsKey(id);
    }
}
