package com.sprint.mission.discodeit.respository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.respository.ChannelRepository;
import java.util.*;

public class JCFChannelRepository implements ChannelRepository {

    private final Map<UUID, Channel> channelMap = new HashMap<>();

    @Override
    public Channel save(Channel channel) {
        channelMap.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        return channelMap.get(id);
    }

    @Override
    public List<Channel> findByName(String name) {
        List<Channel> result = new ArrayList<>();
        for (Channel channel : channelMap.values()) {
            if (channel.getName().equals(name)) {
                result.add(channel);
            }
        }
        return result;
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(channelMap.values());
    }

    @Override
    public void deleteById(UUID id) {
        channelMap.remove(id);
    }
}
