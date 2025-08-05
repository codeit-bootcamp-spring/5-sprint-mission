package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.*;

public class JCFChannelRepository implements ChannelRepository {

    private final Map<UUID, Channel> data = new HashMap<>();

    @Override
    public Channel save(Channel channel) {
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        return data.get(id);
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel update(UUID id, Channel updatedChannel) {
        if (!data.containsKey(id)) {
            throw new NoSuchElementException("Channel not found: " + id);
        }
        data.put(id, updatedChannel);
        return updatedChannel;
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }
}
