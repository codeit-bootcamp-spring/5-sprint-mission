package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.ChannelCreateDto;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    Channel create(ChannelCreateDto dto);

    List<Channel> findAll();

    Channel findById(UUID id);

    List<Channel> findByName(String name);

    Channel updateName(UUID id, String name);

    Channel updateTopic(UUID id, String topic);

    Channel updateDescription(UUID id, String description);

    boolean delete(UUID id);
}
