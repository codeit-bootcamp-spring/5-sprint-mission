package com.sprint.mission.discodeit.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {

    private final Map<UUID, Channel> channels = new HashMap<>();

    @Override
    public void create(Channel channel) {
        channels.put(channel.getId(), channel);
    }

    @Override
    public Channel findById(UUID id) {
        if  (channels.containsKey(id)) {
            return channels.get(id);
        }

        return null;
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(channels.values());
    }

    @Override
    public void update(UUID id, String name, String description, int count) {
        Channel channel = channels.get(id);
        if (channel != null) {
            channel.update(name, description, count);
        }
    }

    @Override
    public void delete(UUID id) {
        channels.remove(id);
    }
}
