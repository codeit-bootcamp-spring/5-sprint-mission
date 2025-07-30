package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data;

    public JCFChannelService() {
        data = new HashMap<>();
    }

    @Override
    public Channel create(ChannelType type, String name, String description) {
        if ( type == null || name == null || name.isBlank() || description == null || description.isBlank()) {
            throw new IllegalArgumentException("channel type or name or description is null or blank");
        }

        Channel channel = new Channel(type, name, description);
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
    public Channel update(UUID channelId, String newName, String newDescription) {
        Channel channel = data.get(channelId);

        if (channel == null) {
            throw new NoSuchElementException("channel not found");
        }

        channel.update(newName, newDescription);
        return channel;
    }

    @Override
    public void delete(UUID channelId) {
        if (!data.containsKey(channelId)) {
            throw new NoSuchElementException("channel not found");
        }
        data.remove(channelId);
    }
}
