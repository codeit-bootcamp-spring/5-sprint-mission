package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final Map<String, Channel> data;

    public JCFChannelService(Map<String, Channel> data) {
        this.data = data;
    }


    @Override
    public Channel createChannel(Channel channel) {
        if (channel.getId() == null) {
            channel.setId(UUID.randomUUID());
        }

        Long currentTime = System.currentTimeMillis();
        channel.setCreateAt(currentTime);
        channel.setUpdateAt(currentTime);

        data.put(channel.getId().toString(), channel);
        return channel;
    }

    @Override
    public Channel getChannelById(UUID id) {
        return data.get(id.toString());
    }

    @Override
    public List<Channel> getAllChannels() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel updateChannel(Channel channel) {
        if (channel.getId() == null || !data.containsKey(channel.getId().toString())) {
            return null;
        }

        Channel existingChannel = data.get(channel.getId().toString());
        channel.setCreateAt(existingChannel.getCreateAt());
        channel.setUpdateAt(System.currentTimeMillis());

        data.put(channel.getId().toString(), channel);
        return channel;
    }

    @Override
    public void deleteChannel(UUID id) {
        data.remove(id.toString());
    }
}
