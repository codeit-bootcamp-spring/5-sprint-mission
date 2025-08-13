package com.codeit.mission.discodeit.repository;

import com.codeit.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelRepository {
    Channel save(Channel channel);

    Optional<Channel> findById(UUID id);

    List<Channel> findAll();

    boolean existsById(UUID id);

    void deleteById(UUID id);
}