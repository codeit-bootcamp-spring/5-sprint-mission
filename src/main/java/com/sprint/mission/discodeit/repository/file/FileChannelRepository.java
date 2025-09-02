package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.file.common.FileUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
@Repository
public class FileChannelRepository implements ChannelRepository {
    private final Path CHANNEL_DIR = Path.of(Channel.class.getSimpleName());

    public FileChannelRepository() {
        FileUtils.init(CHANNEL_DIR);
    }

    @Override
    public Channel save(Channel channel) {
        Path path = CHANNEL_DIR.resolve(channel.getId().toString());
        FileUtils.save(path, channel);
        return channel;
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
    public boolean existsById(UUID id) {
        Path path = CHANNEL_DIR.resolve(id.toString());
        return FileUtils.fileExists(path);
    }

    @Override
    public void delete(UUID id) {
        Path path = CHANNEL_DIR.resolve(id.toString());
        FileUtils.delete(path);
    }

    public void deleteAll() {
        FileUtils.deleteAll(CHANNEL_DIR);
    }
}
