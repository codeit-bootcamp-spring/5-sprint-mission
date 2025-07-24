package com.sprint.mission.discodeit.respository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelRepository {
    Channel save(Channel channel);
    Channel findById(UUID id);
    List<Channel> findByName(String name);
    List<Channel> findAll();
    Channel updateName(UUID id, String name);
    Channel updateTopic(UUID id, String topic);
    void deleteById(UUID id);
}
