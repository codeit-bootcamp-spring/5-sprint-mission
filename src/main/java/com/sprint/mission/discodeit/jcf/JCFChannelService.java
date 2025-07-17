package com.sprint.mission.discodeit.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final List<Channel> channels = new ArrayList<>();

    @Override
    public Channel register(Channel channel) {
        if (channel.getName() == null || channel.getDescription() == null || channel.getName().isBlank() || channel.getDescription().isBlank()) {
            System.out.println("채널 등록 실패!");
            return null;
        }
        channels.add(channel);
        System.out.println("채널 : " + channel.getName() + " 등록 성공");
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        for (Channel channel : channels) {
            if (channel.getId().equals(id)) {
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
    public Channel update(String name, String newDescription) {
        if (newDescription != null && !newDescription.isBlank()) {
            for (Channel channel : channels) {
                if (channel.getName().equals(name)) {
                    channel.setDescription(newDescription);
                    channel.setUpdateAt(System.currentTimeMillis());
                    return channel;
                }
            }
        }
        return null;
    }

    @Override
    public Channel delete(String name) {
        for (Channel channel : channels) {
            if (channel.getName().equals(name)) {
                channels.remove(channel);
                return channel;
            }
        }
        return null;
    }
}
