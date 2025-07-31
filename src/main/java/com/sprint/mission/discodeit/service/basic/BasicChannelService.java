package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class BasicChannelService implements ChannelService {

    private final ChannelRepository repo;

    public BasicChannelService(ChannelRepository repo) {
        this.repo = repo;
    }


    @Override
    public Channel createChannel(String channelname) {
        Channel channel = new Channel(channelname);
        return repo.save(channel);
    }

    @Override
    public Optional<Channel> getChannel(UUID channelId) {
        Optional<Channel> channel = repo.findById(channelId);
        if (channel.isEmpty()) {
            throw new NoSuchElementException("channel with id " + channelId + " not found");
        }
        return channel;
    }

    @Override
    public List<Channel> getAllChannels() {
        return repo.findAll();
    }

    @Override
    public Channel updateChannel(UUID channelId, String channelname) {
        return repo.update(channelId,channelname);
    }

    @Override
    public void deleteChannel(UUID channelId) {
        repo.delete(channelId);
    }
}
