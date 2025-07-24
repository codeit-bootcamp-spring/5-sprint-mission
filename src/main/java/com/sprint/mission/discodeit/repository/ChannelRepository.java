package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.Map;
import java.util.UUID;

public interface ChannelRepository {

    void save(Map<UUID, Channel> data);
    Map<UUID, Channel> loadData();
    void clear();


}
