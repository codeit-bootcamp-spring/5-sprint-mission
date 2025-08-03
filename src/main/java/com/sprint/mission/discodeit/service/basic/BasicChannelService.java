package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;

    public BasicChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @Override
    public Channel create(String channelName, String description) {
        if (channelName == null || channelName.isBlank()) {
            throw new IllegalArgumentException("Channel name cannot be null or blank.");
        }
        if (channelRepository.findByChannelName(channelName).isPresent()) {
            throw new IllegalArgumentException("Channel with name '" + channelName + "' already exists.");
        }

        Channel channel = new Channel(channelName, description);
        return channelRepository.save(channel);
    }

    @Override
    public Channel find(UUID channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public Channel update(UUID channelId, String channelName, String description) {
        Channel channel = find(channelId);
        channel.update(channelName, description);
        return channelRepository.save(channel);
    }

    @Override
    public void delete(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel with id " + channelId + " not found");
        }
        channelRepository.deleteById(channelId);
    }

    @Override
    public void clear() {
        channelRepository.clear();
    }
}
