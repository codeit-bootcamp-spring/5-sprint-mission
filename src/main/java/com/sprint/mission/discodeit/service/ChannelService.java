package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.ChannelDto.Create;
import com.sprint.mission.discodeit.dto.ChannelDto.Update;
import java.util.List;
import java.util.UUID;

public interface ChannelService {

  ChannelDto.Detail create(Create create);

  ChannelDto.Detail update(Update update);

  ChannelDto.Detail findById(UUID id);

  List<ChannelDto.Detail> findAll();

  List<ChannelDto.Detail> findAllByUserId(UUID userId);

  void delete(UUID id);

  void deleteAll();
}
