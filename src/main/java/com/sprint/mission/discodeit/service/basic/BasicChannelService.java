package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class BasicChannelService implements ChannelService {

    ChannelRepository repo;

    public BasicChannelService(ChannelRepository repo) {
        this.repo = repo;
    }

    @Override
    public Channel create(Channel channel) {
        return repo.save(channel);
    }

    @Override
    public Channel updateName(UUID id, String name) {
        Channel channel = searchById(id);
        channel.updateName(name);
        return repo.save(channel);
    }

    @Override
    public Channel updateDescription(UUID id, String description) {
        Channel channel = searchById(id);
        channel.updateDescription(description);
        return  repo.save(channel);
    }

    @Override
    public Channel updateChannelType(UUID id, Channel.ChannelType channelType) {
        Channel channel = searchById(id);
        channel.updateChannelType(channelType);
        return repo.save(channel);
    }

    @Override
    public Channel delete(UUID id) {
        return repo.delete(id).orElseThrow(() -> new NoSuchElementException("해당하는 채널을 찾을 수 없습니다."));
    }

    @Override
    public void deleteAll() {
        repo.deleteAll();
    }

    @Override
    public List<Channel> searchByName(String name) {
        if (repo.searchByName(name).isEmpty()) {
            throw new NoSuchElementException("해당하는 채널을 찾을 수 없습니다.");
        }
        return repo.searchByName(name);
    }

    @Override
    public Channel searchById(UUID id) {
        return repo.searchById(id).orElseThrow(() -> new NoSuchElementException("해당하는 채널을 찾을 수 없습니다."));
    }

    @Override
    public List<Channel> searchAll() {
        return repo.searchAll();
    }
}
