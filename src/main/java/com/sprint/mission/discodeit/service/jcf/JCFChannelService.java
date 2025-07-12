package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data = new HashMap<>();

    public Channel create(String name) {
        Channel channel = new Channel(name);
        data.put(channel.getId(), channel);
        return channel;
    }

    public Channel findById(UUID id) {
        return data.get(id);
    }

    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    public void update(UUID id, String newName) {
        Channel channel = data.get(id);
        if (channel != null) channel.updateName(newName);
    }

    public void delete(UUID id) {
        data.remove(id);
    }
}