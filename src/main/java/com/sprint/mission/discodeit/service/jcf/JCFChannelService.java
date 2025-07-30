package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;
import java.util.stream.Collectors;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> channels = new HashMap<>();

    @Override
    public Channel createChannel(String name, String description) {
        Channel channel = new Channel(name, description);
        channels.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel readChannel(UUID id) {
        return channels.get(id);
    }

    @Override
    public List<Channel> readAllChannels() {
        return channels.values().stream().collect(Collectors.toList());
    }

    @Override
    public Channel updateChannelname(UUID id, String name) {
        Channel channel = channels.get(id);
        if (channel != null) {
            channel.setName(name);
        }
        return channel;
    }

    @Override
    public Channel updateDescription(UUID id, String description) {
        Channel channel = channels.get(id);
        if (channel != null) {
            channel.setDescription(description);
        }
        return channel;
    }

    @Override
    public boolean deleteChannel(UUID id) {
        if (channels.containsKey(id)) {
            channels.remove(id);
            return true;
        }
        return false;
    }
}
