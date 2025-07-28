package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data = new HashMap<>();

    @Override
    public Channel create(Channel channel) {
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        return data.get(id);
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public boolean update(UUID id, Channel updatedChannel) {
        Channel original = data.get(id);
        if (original != null) {
            original.updateName(updatedChannel.getName());
            return true;
        }
        return false;
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }
}
