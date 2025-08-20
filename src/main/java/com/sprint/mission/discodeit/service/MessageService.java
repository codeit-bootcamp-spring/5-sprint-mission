package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.MessageDto.Create;
import com.sprint.mission.discodeit.dto.MessageDto.Update;
import java.util.List;
import java.util.UUID;

public interface MessageService {

  MessageDto.Detail create(Create create);

  MessageDto.Detail update(Update update);

  MessageDto.Detail findById(UUID id);

  List<MessageDto.Detail> findAllByChannelId(UUID channelId);

  void delete(UUID id);

  void deleteAll();
}
