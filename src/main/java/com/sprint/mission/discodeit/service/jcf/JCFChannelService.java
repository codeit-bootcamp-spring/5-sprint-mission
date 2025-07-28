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
        if (original == null) return null; //p1 복사본
        return new Channel(original);
    }

    @Override
    public List<Channel> findAll() {
        //p1 복사본 반환 (불변성 유지)
        List<Channel> result = new ArrayList<>();
        for (Channel c : data.values()) {
            result.add(new Channel(c));
        }
        return result;
    }

    @Override
    public boolean update(UUID id, Channel updatedChannel) {
        if (!data.containsKey(id)) return false;
        // 기존 ID와 타임스탬프 유지, name만 변경한 새 객체로 대체
        Channel current = data.get(id);
        Channel newChannel = new Channel(current.getId(), current.getCreatedAt(), System.currentTimeMillis(), updatedChannel.getName());
        data.put(id, newChannel);
        return true;
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }
}

