package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data = new HashMap<>();

    @Override
    public Channel create(Channel channel) {
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        Channel original = data.get(id);
        if (original == null) return null;
        return new Channel(original); // 복사본 반환
    }

    @Override
    public List<Channel> findAll() {
        List<Channel> result = new ArrayList<>();
        for (Channel c : data.values()) {
            result.add(new Channel(c)); // 복사본 생성
        }
        return result;
    }

    @Override
    public boolean update(UUID id, Channel updatedChannel) {
        if (!data.containsKey(id)) {
            return false;
        }
        data.put(id, updatedChannel);
        return true;
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }
}


