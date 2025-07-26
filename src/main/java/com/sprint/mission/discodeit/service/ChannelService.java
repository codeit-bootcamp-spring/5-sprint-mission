package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {
    Channel create(Channel channel);

    Channel update(Channel channel);

    Channel delete(UUID id);

    void deleteAll();

    List<Channel> searchByName(String name);

    Optional<Channel> searchById(UUID id);

    List<Channel> searchAll();
}
