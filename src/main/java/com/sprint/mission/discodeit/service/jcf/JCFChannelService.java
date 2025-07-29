package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    final Map<UUID, Channel> data = new HashMap<>();

    @Override
    public Channel create(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("channel name is null or blank");
        }

        Channel channel = new Channel(name);
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel find(UUID channelId) {
        if (!data.containsKey(channelId)) {
            throw new NoSuchElementException("channel not found");
        }
        return data.get(channelId);
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel update(UUID channelId, String name) {
        Channel channel = data.get(channelId);

        if (channel == null) {
            throw new NoSuchElementException("channel not found");
        }

        channel.update(name);
        return channel;
    }

    @Override
    public void delete(UUID channelId) {
        data.remove(channelId);
    }
}
