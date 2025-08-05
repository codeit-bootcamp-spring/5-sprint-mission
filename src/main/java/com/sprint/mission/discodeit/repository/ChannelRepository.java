package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository {
    Channel save(Channel channel);
    Optional<Channel> find(UUID channelId);
    List<Channel> findAll();
    boolean existById(UUID channelId);
    void delete(UUID channelId);


}
