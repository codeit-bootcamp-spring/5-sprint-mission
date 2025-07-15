package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final List<Channel> channels = new ArrayList<>();

    @Override
    public Channel createChannel(String name, String description) {
        Channel channel = new Channel(name, description);
        channels.add(channel);
        return channel;
    }

    @Override
    public Channel readChannel(UUID id) {
        for (Channel channel : channels) {
            if (channel.getId().equals(id)) {
                return channel;
            }
        }
        return null;
    }

    @Override
    public List<Channel> readAllChannels() {
        if (!channels.isEmpty()) {
            return channels;
        }
        return null;
    }

    @Override
    public Channel updateChannelname(UUID id, String name) {
        for (Channel channel : channels) {
            if (channel.getId().equals(id)) {
                channel.setName(name);
                channel.update();
                return channel;
            }
        }
        return null;
    }

    @Override
    public Channel updateDescription(UUID id, String description) {
        for (Channel channel : channels) {
            if (channel.getId().equals(id)) {
                channel.setDescription(description);
                channel.update();
                return channel;
            }
        }
        return null;
    }

    @Override
    public boolean deleteChannel(UUID id) {
        for (Channel channel : channels) {
            if (channel.getId().equals(id)) {
                channels.remove(channel);
                return true;
            }
        }
        return false;
    }
}
