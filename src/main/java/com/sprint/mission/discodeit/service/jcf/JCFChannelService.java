package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.main.Channel;
import com.sprint.mission.discodeit.entity.enums.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {

    private final Map<UUID, Channel> data;

    public JCFChannelService() {
        this.data = new HashMap<>();
    }

    @Override
    public Channel create(String name, String description, ChannelType type) {
        Channel channel = new Channel(
                name,
                description,
                type
        );
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        return Optional.ofNullable(data.get(id))
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + id + " not found"));
    }

    @Override
    public List<Channel> findAll() {
        return data.values().stream().toList();
    }

    @Override
    public Channel update(UUID id, String name, String description, ChannelType type) {
        Channel channel = Optional.ofNullable(findById(id))
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + id + " not found"));
        channel.update(name, description, type);
        return channel;
    }

    @Override
    public void delete(UUID id) {
        Channel channel = findById(id);
        data.remove(channel.getId());
    }
}