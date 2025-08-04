package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository {
    Channel save(Channel channel);

    Optional<Channel> delete(UUID id);

    void deleteAll();

    List<Channel> searchByName(String name);

    Optional<Channel> searchById(UUID id);

    List<Channel> searchAll();
}
