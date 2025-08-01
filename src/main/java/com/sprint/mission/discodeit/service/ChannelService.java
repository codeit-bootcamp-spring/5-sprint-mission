package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel create(Channel channel);

    Channel create(ChannelType type, String name, String description, UUID adminUserId);

    List<Channel> getAll();

    Channel get(UUID id);

    Channel update(UUID id, String name, String description);


    // TODO mission 3 인터페이스 정리 예정 : create, find, findall, update, delete
    ChannelDto.DetailResponse create(ChannelDto.CreateRequest request);

    ChannelDto.DetailResponse update(ChannelDto.UpdateRequest request);

    ChannelDto.DetailResponse findById(UUID id);

    List<ChannelDto.DetailResponse> findAll();

    List<ChannelDto.DetailResponse> findAllByUserId(UUID userId);

    void delete(UUID id);

    void deleteAll();
}
