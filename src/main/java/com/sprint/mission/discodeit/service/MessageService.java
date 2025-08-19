package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

public interface MessageService {

  Message create(@Valid MessageCreateRequest messageCreateRequest,
      @Valid List<BinaryContentCreateRequest> binaryContentCreateRequests);

  Message findById(UUID messageId);

  List<Message> findAllByChannelId(UUID channelId);

  Message update(@Valid MessageUpdateRequest messageUpdateRequest);

  void delete(UUID messageId);
}
