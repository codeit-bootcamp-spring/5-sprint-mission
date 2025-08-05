package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;

    public BasicChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @Override
    public Channel createChannel(ChannelType type, String name) {
        Channel channel = new Channel(type, name);
        return channelRepository.save(channel);
    }

    @Override
    public Channel find(UUID channelId) {
        return channelRepository.findById(channelId).orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public Channel update(UUID channelId,ChannelType type, String name) {
        Channel channel = channelRepository.findById(channelId).orElseThrow(() ->
                new NoSuchElementException("Channel with id " + channelId + " not found"));

        channel.update(type,name);
        return channelRepository.save(channel);
    }

    @Override
    public Channel delete(UUID channelId) {
        Channel channel = channelRepository.findById(channelId).orElseThrow(() ->
                new NoSuchElementException("Channel with id " + channelId + " not found"));

        return channelRepository.delete(channelId);
    }
}
