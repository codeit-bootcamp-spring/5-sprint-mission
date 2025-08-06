package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
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
    public Channel create(ChannelType type, String name, String description) {
        Channel channel = new Channel(type, name, description);
        return repo.save(channel);
    }

    @Override
    public Optional<Channel> find(UUID id) {
        return repo.findById(id);
    }

    @Override
    public List<Channel> findAll() {
        return repo.findAll();
    }

    @Override
    public Channel update(UUID id, String name, String description) {
        Channel channel = repo.findById(id).orElse(null);
        channel.update(name, description);
        return repo.save(channel);
    }

    @Override
    public void delete(UUID id) {
        repo.delete(id);
    }
}
