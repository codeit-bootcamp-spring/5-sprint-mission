package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.*;

public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> data;

    public JCFChannelRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public Channel save(Channel channel) {
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Optional<Channel> find(UUID channelId) {
        return Optional.ofNullable(data.get(channelId));
    }

    @Override
    public List<Channel> findAll() {
        return this.data.values().stream().toList();
    }

    @Override
    public boolean existById(UUID channelId) {
        return data.containsKey(channelId);
    }

    @Override
    public void delete(UUID channelId) {
        if (!this.data.containsKey(channelId)) {
            throw new NoSuchElementException("Channel with id " + channelId + " not found");
        }
        this.data.remove(channelId);
    }
}
