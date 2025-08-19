package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.ChannelDto.Create;
import com.sprint.mission.discodeit.dto.ChannelDto.Update;
import java.util.List;
import java.util.UUID;

public interface ChannelService {

  ChannelDto.DetailResponse create(Create create);

  ChannelDto.DetailResponse update(Update update);

  ChannelDto.DetailResponse findById(UUID id);

  List<ChannelDto.DetailResponse> findAll();

  List<ChannelDto.DetailResponse> findAllByUserId(UUID userId);

  void delete(UUID id);

  void deleteAll();
}
