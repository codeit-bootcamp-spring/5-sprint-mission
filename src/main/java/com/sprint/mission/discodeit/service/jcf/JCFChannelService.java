package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.HashMap;
import java.util.UUID;

public class JCFChannelService implements ChannelService {

    private final HashMap<UUID, Channel> data;

    public JCFChannelService(HashMap<UUID, Channel> data) {
        this.data = data;
    }

    @Override
    public void addChannel(Channel channel) {
        data.put(channel.getId(), channel);
    }

    @Override
    public void updateChannel(Channel channel) {
        data.put(channel.getId(), channel);
    }

    @Override
    public void deleteChannel(UUID id) {
        data.remove(id);
    }

    @Override
    public Channel getChannel(UUID id) {
        return data.get(id);
    }

    @Override
    public HashMap<UUID, Channel> getAllChannels() {
        return data;
    }
}
