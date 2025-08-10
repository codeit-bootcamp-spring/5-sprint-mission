package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageDeleteRequest;
import com.sprint.mission.discodeit.dto.response.MessageResponseDto;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageResponseDto create(MessageCreateRequest request);
    MessageResponseDto find(UUID messageId);
    List<MessageResponseDto> findAllByChannelId(UUID channelId);
    MessageResponseDto update(MessageUpdateRequest request);
    void delete(MessageDeleteRequest request);
}
