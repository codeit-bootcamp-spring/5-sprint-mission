package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    final List<Channel> data;

    public JCFChannelService() {
        data = new ArrayList<>();
    }

    @Override
    public Channel create(Channel channel) {

        if (channel != null) {
            data.add(channel);
            return channel;
        }

        return null;
    }

    @Override
    public List<Channel> getAll() {
        return data;
    }

    @Override
    public Channel get(UUID id) {
        return data.stream().filter(c -> c.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public Channel update(UUID id, String name, String description) {
        Channel channel = data.stream().filter(c -> c.getId().equals(id)).findFirst().orElse(null);
        if (channel != null) {
            channel.update(name, description);
            return channel;
        }

        return null;
    }

    @Override
    public void delete(UUID id) {
        Channel channel = data.stream().filter(c -> c.getId().equals(id)).findFirst().orElse(null);
        if (channel != null) {
            data.remove(channel);
        }
    }
}
