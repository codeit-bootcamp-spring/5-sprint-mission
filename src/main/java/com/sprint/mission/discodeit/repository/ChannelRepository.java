package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.*;

public interface ChannelRepository {
    Channel save(Channel channel);

    Optional<Channel> findById(UUID id);

    List<Channel> findAll();

    Channel update(UUID id, String content);

    Channel delete(UUID id);

    boolean existById(UUID id);
}
