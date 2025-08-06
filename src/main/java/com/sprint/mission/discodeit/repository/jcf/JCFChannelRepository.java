package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.*;

public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> channelMap;

    public JCFChannelRepository() {
        channelMap = new HashMap<>();
    }

    @Override
    public Channel save(Channel channel) {
        channelMap.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID channelId) {
        return Optional.ofNullable(channelMap.get(channelId));
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(channelMap.values());
    }

    @Override
    public boolean existsById(UUID channelId) {
        return this.channelMap.containsKey(channelId);
    }

    @Override
    public boolean delete(UUID channelId) {
        return channelMap.remove(channelId, channelMap.get(channelId));
    }
}
