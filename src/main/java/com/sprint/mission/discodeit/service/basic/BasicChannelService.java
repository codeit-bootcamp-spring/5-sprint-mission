package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.UUID;

public class BasicChannelService implements ChannelService {

    private final ChannelRepository repository;

    public BasicChannelService(ChannelRepository repository) {
        this.repository = repository;
    }

    @Override
    public Channel create(Channel channel) {
        repository.save(channel);
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<Channel> findAll() {
        return repository.findAll();
    }

    @Override
    public boolean update(UUID id, Channel channel) {
        Channel result = repository.update(id, channel);
        return result != null;
    }

    @Override
    public void delete(UUID id) {
        repository.delete(id);
    }
}
