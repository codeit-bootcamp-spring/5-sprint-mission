package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.MessageDto.Create;
import com.sprint.mission.discodeit.dto.MessageDto.Update;
import java.util.List;
import java.util.UUID;

public interface MessageService {

  MessageDto.DetailResponse create(Create create);

  MessageDto.DetailResponse update(Update update);

  MessageDto.DetailResponse findById(UUID id);

  List<MessageDto.DetailResponse> findAllByChannelId(UUID channelId);

  void delete(UUID id);

  void deleteAll();
}
