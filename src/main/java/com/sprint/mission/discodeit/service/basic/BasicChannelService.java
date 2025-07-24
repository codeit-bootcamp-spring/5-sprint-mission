package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.UUID;

public class BasicChannelService implements ChannelService {

    ChannelRepository repo;

    public BasicChannelService(ChannelRepository repo) {
        this.repo = repo;
    }

    @Override
    public void create(Channel channel) {
        repo.save(channel);
    }

    @Override
    public void update(Channel channel) {
        repo.delete(channel);
        repo.save(channel);
    }

    @Override
    public void delete(Channel channel) {
        repo.delete(channel);
    }

    @Override
    public void deleteAll() {
        repo.deleteAll();
    }

    @Override
    public List<Channel> searchByName(String name) {
        return repo.searchByName(name);
    }

    @Override
    public Channel searchById(UUID id) {
        return repo.searchById(id);
    }

    @Override
    public List<Channel> searchAll() {
        return repo.searchAll();
    }
}
