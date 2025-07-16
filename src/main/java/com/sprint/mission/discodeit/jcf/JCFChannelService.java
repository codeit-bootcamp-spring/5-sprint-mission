package com.sprint.mission.discodeit.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.ArrayList;
import java.util.List;

public class JCFChannelService  implements ChannelService {
    private final List<Channel> channels = new ArrayList<>();

    @Override
    public boolean register(Channel channel) {
       if (channel.getName() == null || channel.getDescription()  == null || channel.getName().isBlank() || channel.getDescription().isBlank())  {
           return false;
       }
        channels.add(channel);
        return true;
    }

    @Override
    public Channel findByName(String name) {
        for (Channel channel : channels) {
            if (channel.getName().equals(name)) {
                return channel;
            }
        }
        return null;
    }

    @Override
    public List<Channel> findAll() {
        return List.copyOf(channels);
    }

    @Override
    public boolean update(String name, String description) {
        for (Channel channel : channels) {
            if (channel.getName().equals(name)) {
                channel.setDescription(description);
                channel.setUpdateAt(System.currentTimeMillis());
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean delete(String name) {
        for (Channel channel : channels) {
            if (channel.getName().equals(name)) {
                channels.remove(channel);
                return true;
            }
        }
        return false;
    }
}
