package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.ChannelDto.CreateCommand;
import com.sprint.mission.discodeit.dto.ChannelDto.UpdateCommand;
import java.util.List;
import java.util.UUID;

public interface ChannelService {

  ChannelDto.Detail create(CreateCommand create);

  ChannelDto.Detail update(UpdateCommand update);

  ChannelDto.Detail findById(UUID id);

  List<ChannelDto.Detail> findAll();

  List<ChannelDto.Detail> findAllByUserId(UUID userId);

  void delete(UUID id);

  void deleteAll();
}
