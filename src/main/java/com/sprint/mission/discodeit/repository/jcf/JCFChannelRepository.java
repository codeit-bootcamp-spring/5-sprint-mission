package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.*;

public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> data;

    public JCFChannelRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public Channel save(Channel channel) {
        return data.put(channel.getId(), channel);
    }

    @Override
    public Optional<Channel> delete(UUID id) {
        return Optional.ofNullable(data.remove(id));
    }

    @Override
    public void deleteAll() {
        data.clear();
    }

    @Override
    public List<Channel> searchByName(String name) {
        List<Channel> channels = new ArrayList<>();
        for (Channel channel : data.values()) {
            if (channel.getName().contains(name)) {
                channels.add(channel);
            }
        }
        return channels;
    }

    @Override
    public Optional<Channel> searchById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Channel> searchAll() {
        return new ArrayList<>(data.values());
    }
}
