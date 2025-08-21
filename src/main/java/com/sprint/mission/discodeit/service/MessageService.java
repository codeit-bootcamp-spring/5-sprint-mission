package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageResponseDto;
import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageResponseDto create(MessageCreateRequest request, List<BinaryContentCreateRequest> binaryContentCreateRequest);
    MessageResponseDto find(UUID messageId);
    List<MessageResponseDto> findAllByChannelId(UUID channelId);
    MessageResponseDto update(UUID messageId, MessageUpdateRequest request);
    void delete(UUID messageId);
}
