package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class FileChannelService implements ChannelService {

    private final FileChannelRepository repo;

    public FileChannelService(FileChannelRepository repo) {
        this.repo = repo;
    }


    @Override
    public Channel create(ChannelType type, String name, String description) {
        Channel channel = new Channel(type, name, description);
        return repo.save(channel);
    }

    @Override
    public Optional<Channel> find(UUID id) {
        Optional<Channel> channel = repo.findById(id);
        if (channel.isEmpty()) {
            throw new NoSuchElementException("Channel with id " + id + " does not exist");
        }
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
