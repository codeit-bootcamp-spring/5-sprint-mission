package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {

    Channel create(ChannelDto.Create dto);

    List<Channel> findAll();

    Channel findById(UUID id);

    List<Channel> findByName(String name);

    Channel updateName(UUID id, String name);

    Channel updateTopic(UUID id, String topic);

    Channel updateDescription(UUID id, String description);

    boolean delete(UUID id);
}
