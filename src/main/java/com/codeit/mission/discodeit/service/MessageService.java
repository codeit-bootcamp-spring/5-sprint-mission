package com.codeit.mission.discodeit.service;

import com.codeit.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.codeit.mission.discodeit.dto.request.MessageCreateRequest;
import com.codeit.mission.discodeit.dto.request.MessageUpdateRequest;
import com.codeit.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(MessageCreateRequest messageCreateRequest, List<BinaryContentCreateRequest> binaryContentCreateRequests);

    Message find(UUID messageId);

    List<Message> findAllByChannelId(UUID channelId);

    Message update(UUID messageId, MessageUpdateRequest request);

    void delete(UUID messageId);
}
