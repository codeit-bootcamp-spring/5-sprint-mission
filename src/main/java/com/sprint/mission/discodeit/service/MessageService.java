package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.MessageDto.CreateCommand;
import com.sprint.mission.discodeit.dto.MessageDto.Detail;
import com.sprint.mission.discodeit.dto.MessageDto.UpdateCommand;
import com.sprint.mission.discodeit.dto.PageResponse;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface MessageService {

  MessageDto.Detail create(CreateCommand create);

  MessageDto.Detail update(UpdateCommand update);

  MessageDto.Detail findById(UUID id);

  PageResponse<MessageDto.Detail> findAllByChannelId(UUID channelId, Instant cursor,
      Pageable pageable);

  void delete(UUID id);

  void deleteAll();
}
