package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.neutral.MessageCreateCommand;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

public interface MessageService {

  MessageDto create(@Valid MessageCreateCommand messageCreateCommand);

  MessageDto findById(UUID messageId);

  List<MessageDto> findAllByChannelId(UUID channelId);

  PageResponse<MessageDto> findAllByChannelId(UUID channelId, Pageable pageable);

  MessageDto update(UUID messageId, @Valid MessageUpdateRequest messageUpdateRequest);

  void delete(UUID messageId);
}
