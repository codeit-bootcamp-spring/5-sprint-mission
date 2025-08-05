package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.*;

public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> data = new HashMap<>();

    @Override
    public Channel save(Channel channel) {
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel find(UUID channelId) {
        Channel channelNullable = this.data.get(channelId);
        return Optional.ofNullable(channelNullable)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " not found"));
    }

    @Override
    public List<Channel> findAll() {
        return this.data.values().stream().toList();
    }

    @Override
    public void delete(UUID channelId) {
        if (!this.data.containsKey(channelId)) {
            throw new NoSuchElementException("Channel with id " + channelId + " not found");
        }
        this.data.remove(channelId);
    }
}
