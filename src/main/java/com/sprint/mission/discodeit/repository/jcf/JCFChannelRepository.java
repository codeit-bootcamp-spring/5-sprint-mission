package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.*;
import java.util.stream.Collectors;

public class JCFChannelRepository implements ChannelRepository {

    private Map<UUID, Channel> channels = new HashMap<>();

    @Override
    public Channel save(Channel channel) {
        channels.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        if (channels.containsKey(id)) {
            return Optional.of(channels.get(id));
        }
        return Optional.empty();
    }

    @Override
    public List<Channel> findAll() {
        return channels.values().stream().collect(Collectors.toList());
    }

    @Override
    public Channel update(UUID id, Channel channel) {
        if (!channels.containsKey(id)) {
            throw new NoSuchElementException();
        }
        channels.put(id, channel);
        return channel;
    }

    @Override
    public boolean existsById(UUID id) {
        if (channels.containsKey(id)) {
            return true;
        }
        return false;
    }

    @Override
    public void deleteById(UUID id) {
        channels.remove(id);
    }
}
