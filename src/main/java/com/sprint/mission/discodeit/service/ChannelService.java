package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    ChannelDto.response create(ChannelDto.create dto);

    ChannelDto.response update(ChannelDto.update dto);

    Channel createPrivate(ChannelDto.createPrivate dto);

    List<Channel> findAll();

    Channel findById(UUID id);


    List<Channel> findByName(String name);

    boolean delete(UUID id);
}
