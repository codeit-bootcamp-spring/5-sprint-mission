package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {

    private final Map<UUID, Channel> data;

    public JCFChannelService() {
        data = new HashMap<>();
    }

    @Override
    public Channel create(Channel channel) {
        return data.put(channel.getId(), channel);
    }

    @Override
    public Channel update(Channel channel) {
        return data.put(channel.getId(), channel);
    }

    @Override
    public Channel delete(UUID id) {
        return data.remove(id);
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
