package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.file.common.FileUtils;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileChannelRepository implements ChannelRepository {
    private final Path CHANNEL_DIR = Path.of("channel");

    public FileChannelRepository() {
        FileUtils.init(CHANNEL_DIR);
    }

    @Override
    public Channel save(Channel channelDto) {
        Path path = CHANNEL_DIR.resolve(channelDto.getId().toString());
        FileUtils.save(path, channelDto);
        return channelDto;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        Path path = CHANNEL_DIR.resolve(id.toString());
        return Optional.ofNullable(FileUtils.findOne(path, Channel.class));
    }

    @Override
    public List<Channel> findAll() {
        return FileUtils.findAll(CHANNEL_DIR, Channel.class);
    }

    @Override
    public void delete(UUID id) {
        Path path = CHANNEL_DIR.resolve(id.toString());
        FileUtils.delete(path);
    }

    @Override
    public void deleteAll() {
        FileUtils.deleteAll(CHANNEL_DIR);
    }
}
