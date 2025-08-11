package com.sprint.mission.discodeit.respository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.respository.ChannelRepository;

import java.util.*;

public class JCFChannelRepository implements ChannelRepository {

    private final Map<UUID, Channel> data = new HashMap<>();

    @Override
    public Channel save(Channel channel) {
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Channel> findByName(String name) {
        return data.values().stream()
                .filter(c -> c.getName() != null)
                .filter(c -> c.getName().equals(name))
                .toList();
    }

    @Override
    public List<Channel> findAll() {
        return List.copyOf(data.values());
    }

    @Override
    public Optional<Channel> updateName(UUID id, String name) {
        Channel channel = data.get(id);
        if (channel != null) {
            channel.updateName(name);
            return Optional.of(channel);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Channel> updateTopic(UUID id, String topic) {
        Channel channel = data.get(id);
        if (channel != null) {
            channel.updateTopic(topic);
            return Optional.of(channel);
        }
        return Optional.empty();
    }

    @Override
    public boolean delete(UUID id) {
        return data.remove(id) != null;
    }
}
