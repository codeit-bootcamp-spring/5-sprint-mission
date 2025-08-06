package com.sprint.mission.discodeit.service.jcf;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;


public class JCFChannelService implements ChannelService {

    private final ChannelRepository repo;

    public JCFChannelService(ChannelRepository repo) {
        this.repo = repo;
    }


    @Override
    public Channel create(ChannelType type, String name, String description) {
        Channel channel = new Channel(type, name, description);
        repo.save(channel);
        return channel;
    }

    @Override
    public Optional<Channel> find(UUID id) {
        Optional<Channel> channel = repo.findById(id);
        if (channel.isEmpty()) {
            throw new NoSuchElementException("Channel with id " + id + " not found");
        }
        return channel;
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
