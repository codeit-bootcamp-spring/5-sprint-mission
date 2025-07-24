package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class FileChannelService implements ChannelService {

    ChannelRepository repo;

    public FileChannelService(ChannelRepository repo) {
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
        List<Channel> channels = repo.searchByName(name);
        if (channels.isEmpty()) {
            System.err.println("해당하는 채널을 찾을 수 없습니다.");
            throw new NoSuchElementException();
        }
        return channels;
    }

    @Override
    public Channel searchById(UUID id) {
        Channel channel = repo.searchById(id).orElse(null);
        if (channel == null) {
            System.err.println("해당하는 채널을 찾을 수 없습니다.");
            throw new NoSuchElementException();
        }
        return channel;
    }

    @Override
    public List<Channel> searchAll() {
        return repo.searchAll();
    }
}
