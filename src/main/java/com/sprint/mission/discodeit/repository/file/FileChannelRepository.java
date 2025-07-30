package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileChannelRepository implements ChannelRepository {

    private static final String directoryName = "channels";

    @Override
    public Optional<Channel> save(Channel channel) {
        return Optional.empty();
    }

    @Override
    public Optional<Channel> findById(UUID channelId) {
        return Optional.empty();
    }

    @Override
    public List<Channel> findAll() {
        return List.of();
    }

    @Override
    public void delete(Channel channel) {

    }

    @Override
    public void deleteAll() {

    }
}
