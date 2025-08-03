package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class FileChannelService implements ChannelService {

    private final ChannelRepository channelRepository;

    public FileChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @Override
    public Channel create(String channelName, String description) {
        Channel channel = new Channel(channelName, description);
        return channelRepository.save(channel);
    }

    @Override
    public Channel find(UUID channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel not found"));
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
        channelRepository.deleteById(channelId);
    }

    @Override
    public void clear() {
        channelRepository.clear();
    }
}