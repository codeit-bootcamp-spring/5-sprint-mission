package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {

    private final Map<UUID, Channel> data;

    public JCFChannelService() {
        data = new HashMap<>();
    }

    @Override
    public Channel create(Channel channel) {
        return data.put(channel.getId(), channel);
    }

    @Override
    public Channel updateName(UUID id, String name) {
        Channel channel = searchById(id);
        channel.updateName(name);
        return data.put(channel.getId(), channel);
    }

    @Override
    public Channel updateDescription(UUID id, String description) {
        Channel channel = searchById(id);
        channel.updateDescription(description);
        return data.put(channel.getId(), channel);
    }

    @Override
    public Channel updateChannelType(UUID id, Channel.ChannelType channelType) {
        Channel channel = searchById(id);
        channel.updateChannelType(channelType);
        return data.put(channel.getId(), channel);
    }

    @Override
    public Channel delete(UUID id) {
        Channel channel = searchById(id);
        return data.remove(channel.getId());
    }

    @Override
    public void deleteAll() {
        data.clear();
    }

    @Override
    public List<Channel> searchByName(String name) {
        List<Channel> channels = new ArrayList<>();
        for (Channel channel : data.values()) {
            if (channel.getName().contains(name)) {
                channels.add(channel);
            }
        }
        if(channels.isEmpty()) {
            throw new NoSuchElementException("해당하는 채널을 찾을 수 없습니다.");
        }
        return channels;
    }

    @Override
    public Channel searchById(UUID id) {
        if (!data.containsKey(id)) {
            throw new NoSuchElementException("해당하는 채널을 찾을 수 없습니다.");
        }
        return data.get(id);
    }

    @Override
    public List<Channel> searchAll() {
        return new ArrayList<>(data.values());
    }
}
