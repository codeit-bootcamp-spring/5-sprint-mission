package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {

    private final List<Channel> data;

    public JCFChannelService() {
        data = new ArrayList<>();
    }

    @Override
    public void create(Channel channel) {
        data.add(channel);
    }

    @Override
    public void update(Channel channel) {
        int i = data.indexOf(channel);
        data.set(i, channel);
    }

    @Override
    public void delete(Channel channel) {
        data.remove(channel);
    }

    @Override
    public void deleteAll() {
        data.clear();
    }

    @Override
    public Channel searchByIndex(int i) {
        return data.get(i);
    }

    @Override
    public List<Channel> searchByName(String name) {
        List<Channel> channels = new ArrayList<>();
        for (Channel channel : data) {
            if (channel.getName().contains(name)) {
                channels.add(channel);
            }
        }

        return channels;
    }

    @Override
    public Channel searchById(UUID id) {
        for (Channel channel : data) {
            if (channel.getId().equals(id)) {
                return channel;
            }
        }
        return null;
    }


    @Override
    public List<Channel> searchAll() {
        return data;
    }

    @Override
    public String toString() {
        return "JCFChannelService{" +
                "data=" + data +
                '}';
    }
}
