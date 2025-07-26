package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class JCFChannelService implements ChannelService {
    private final List<Channel> channels;

    public JCFChannelService() {
        this.channels = new ArrayList<>();
    }

    @Override
    public Channel createChannel(String channelName, String channelDescription) {
        Channel channel = new Channel(UUID.randomUUID(), channelName, channelDescription, Instant.now().getEpochSecond());
        channels.add(channel);

        return channel;
    }

    @Override
    public Channel findById(UUID channelId) {
        return channels.stream()
                .filter(channel -> channel.getId().equals(channelId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Channel> findByChannelName(String channelName) {
        return channels.stream()
                .filter(channel -> channel.getChannelName().contains(channelName))
                .collect(Collectors.toList());
    }

    @Override
    public List<Channel> findAllChannels() {
        return channels;
    }

    @Override
    public Channel updateById(UUID channelId, String channelName, String channelDescription) {
        for (Channel channel : channels) {
            if (channel.getId().equals(channelId)) {
                channel.updateChannel(channelName, channelDescription, Instant.now().getEpochSecond());

                return channel;
            }
        }
        return null;
    }

    @Override
    public boolean deleteById(UUID channelId) {
        return channels.remove(findById(channelId));
    }
}
