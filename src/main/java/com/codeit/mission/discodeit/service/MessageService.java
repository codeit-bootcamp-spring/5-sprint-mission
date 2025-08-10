package com.codeit.mission.discodeit.service;

import com.codeit.mission.discodeit.dto.message.MessageCreateRequest;
import com.codeit.mission.discodeit.dto.message.MessageResponse;
import com.codeit.mission.discodeit.dto.message.MessageUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageResponse create(MessageCreateRequest request);

    MessageResponse find(UUID messageId);

    List<MessageResponse> findAllByChannelId(UUID channelId);

    MessageResponse update(MessageUpdateRequest request);

    void delete(UUID messageId);
}
