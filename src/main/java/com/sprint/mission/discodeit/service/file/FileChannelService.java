package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileChannelService implements ChannelService {

    private final FileChannelRepository fileRepo;

    public FileChannelService(FileChannelRepository fileRepo) {
        this.fileRepo = fileRepo;
    }

    @Override
    public Channel createChannel(String channelname) {
        return fileRepo.save(new Channel(channelname));
    }

    @Override
    public Optional<Channel> getChannel(UUID channelId) {
        return fileRepo.findById(channelId);
    }

    @Override
    public List<Channel> getAllChannels() {
        return fileRepo.findAll();
    }

    @Override
    public Channel updateChannel(UUID channelId, String channelname) {
        return fileRepo.update(channelId, channelname);
    }

    @Override
    public void deleteChannel(UUID channelId) {
        fileRepo.delete(channelId);
    }
}
