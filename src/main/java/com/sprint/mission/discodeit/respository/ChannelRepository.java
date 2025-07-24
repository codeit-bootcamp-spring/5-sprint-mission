package com.sprint.mission.discodeit.respository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelRepository {
    Channel save(Channel channel);
    Channel findById(UUID id);
    List<Channel> findByName(String name);
    List<Channel> findAll();
    void deleteById(UUID id);
}
