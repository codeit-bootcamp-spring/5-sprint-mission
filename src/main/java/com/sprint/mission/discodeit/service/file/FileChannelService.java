package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.UUID;

public class FileChannelService implements ChannelService {
    private final FileChannelRepository channelRepo;

    public FileChannelService(FileChannelRepository channelRepo) {
        this.channelRepo = channelRepo;
    }

    @Override
    public Channel create(ChannelType type, String name, String description) {
        Channel channel = new Channel(type, name, description);
        return channelRepo.save(channel);
    }

    @Override
    public Channel find(UUID channelId) {
        return channelRepo.find(channelId);
    }

    @Override
    public List<Channel> findAll() {
        return channelRepo.findAll();
    }

    @Override
    public Channel update(UUID channelId, String newName, String newDescription) {
        Channel channel = channelRepo.find(channelId);
        channel.update(newName, newDescription);
        return channelRepo.save(channel);
    }

    @Override
    public void delete(UUID channelId) {
        channelRepo.delete(channelId);
    }
}
