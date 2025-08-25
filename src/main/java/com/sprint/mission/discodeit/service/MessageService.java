package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.MessageDto.CreateCommand;
import com.sprint.mission.discodeit.dto.MessageDto.UpdateCommand;
import java.util.List;
import java.util.UUID;

public interface MessageService {

  MessageDto.Detail create(CreateCommand create);

  MessageDto.Detail update(UpdateCommand update);

  MessageDto.Detail findById(UUID id);

  List<MessageDto.Detail> findAllByChannelId(UUID channelId);

  void delete(UUID id);

  void deleteAll();
}
