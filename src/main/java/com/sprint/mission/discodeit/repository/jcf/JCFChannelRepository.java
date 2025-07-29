package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.*;

public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> storage = new HashMap<>();

    @Override
    public Channel save(Channel channel) {
        channel.updateTimestamp();               // 수정 시간 갱신
        storage.put(channel.getId(), channel);   // UUID 기반 저장
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        return storage.get(id);
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void deleteById(UUID id) {
        storage.remove(id);
    }
}
