package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data = new HashMap<>();

    public void create(Channel channel) {
        data.put(channel.getId(), channel);
    }

    public Channel read(UUID id) {
        return data.get(id);
    }

    public List<Channel> readAll() {
        return new ArrayList<>(data.values());
    }

    public void update(UUID id, String name) {
        Channel channel = data.get(id);
        if (channel != null) channel.update(name);
    }

    public void delete(UUID id) {
        data.remove(id);
    }
}
