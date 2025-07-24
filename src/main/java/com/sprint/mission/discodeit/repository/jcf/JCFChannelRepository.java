package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> data = new HashMap<>();
    @Override
    public void save(Map<UUID, Channel> data) {
        this.data.clear();
        this.data.putAll(data);
    }

    @Override
    public Map<UUID, Channel> loadData() {
        return new HashMap<>(data);
    }

    @Override
    public void clear() {
        this.data.clear();
    }
}
