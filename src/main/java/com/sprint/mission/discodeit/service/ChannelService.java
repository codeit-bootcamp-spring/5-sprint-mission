package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.enums.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    Channel create(String name, String description, ChannelType type);
    Channel findById(UUID id);
    List<Channel> findAll();
    Channel update(UUID id, String name, String description, ChannelType type);
    void delete(UUID id);
}
