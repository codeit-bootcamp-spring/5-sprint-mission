package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.IOException;
import java.util.*;

public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> data = new HashMap<>();

    @Override
    public void save(Channel channel) {
        data.put(channel.getId(), channel);
    }

    @Override
    public Channel findById(UUID id) {
        return data.get(id);
    }

    @Override
    public Channel findByName(String name) {
        if (name == null || name.isBlank()) return null;
        for (Channel channel : data.values()) {
            if (channel.getName().equals(name)) return channel;
        }
        return null;
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void update(Channel channel) {
        if (data.containsKey(channel.getId())) {
            data.put(channel.getId(), channel);
        }
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }
}
