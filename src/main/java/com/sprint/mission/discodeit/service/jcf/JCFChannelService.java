package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JCFChannelService implements ChannelService {

    private final Map<UUID, Channel> data;

    public JCFChannelService() {
        this.data = new HashMap<>();
    }


    @Override
    public void save(Channel channel) {
        data.put(channel.getId(), channel);
    }

    @Override
    public Channel find(UUID id) {
        return data.get(id);
    }

    @Override
    public ArrayList<Channel> allFind() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void update(UUID id, Channel channel) {
        if (data.containsKey(id)){
            data.put(id, channel);
        }
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }
}
