package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelDto;
import java.util.List;
import java.util.UUID;

public interface ChannelService {

  ChannelDto.DetailResponse create(ChannelDto.CreateRequest request);

  ChannelDto.DetailResponse update(ChannelDto.UpdateRequest request);

  ChannelDto.DetailResponse findById(UUID id);

  List<ChannelDto.DetailResponse> findAll();

  List<ChannelDto.DetailResponse> findAllByUserId(UUID userId);

  void delete(UUID id);

  void deleteAll();
}
