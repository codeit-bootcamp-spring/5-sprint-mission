package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.UUID;
import java.util.NoSuchElementException;

public class FileChannelService implements ChannelService {
    private final ChannelRepository repository;

    public FileChannelService(ChannelRepository repository) {
        this.repository = repository;
    }

    @Override
    public Channel create(Channel channel) {
        return repository.save(channel);
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
    public boolean update(UUID id, Channel updatedChannel) {
        try {
            repository.update(id, updatedChannel);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    @Override
    public void delete(UUID id) {
        repository.delete(id);
    }
}
