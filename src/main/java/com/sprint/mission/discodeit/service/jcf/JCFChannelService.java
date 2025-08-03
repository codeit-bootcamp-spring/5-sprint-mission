package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> channelMap;

    public JCFChannelService() {
        channelMap = new HashMap<>();
    }

    @Override
    public Channel create(ChannelType type, String name, String description) {
        if (type ==null || name == null || name.isBlank() || description == null || description.isBlank()) {
            throw new IllegalArgumentException("Channel info is invalid");
        }
        Channel channel = new Channel(type, name, description);
        channelMap.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel find(UUID channelId) {
        Channel channel = channelMap.get(channelId);
        if (channel == null) {
            throw new NoSuchElementException("Channel not found");
        }
        return channel;
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(channelMap.values());
    }

    @Override
    public Channel update(UUID channelId, String name, String description) {
        Channel channel = channelMap.get(channelId);
        if (channel == null) {
            throw new NoSuchElementException("Channel not found");
        }
        channel.update(name, description);
        return channel;
    }

    @Override
    public boolean delete(UUID channelId) {
        return channelMap.remove(channelId, find(channelId));
    }
}
