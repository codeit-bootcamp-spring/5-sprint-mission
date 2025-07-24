package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    void create(Channel channel);

    void update(Channel channel);

    void delete(Channel channel);

    void deleteAll();

    Channel searchByIndex(int i);

    List<Channel> searchByName(String name);

    Channel searchById(UUID id);

    List<Channel> searchAll();
}
