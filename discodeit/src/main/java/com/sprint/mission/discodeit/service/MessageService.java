package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(MessageCreateRequest request);
    Message create(MessageCreateRequest request, BinaryContent binaryContent);
    Message create(MessageCreateRequest request, List<BinaryContent> binaryContent);
    Message find(UUID messageId);
    List<Message> findallByChannelId(UUID channelId);
    Message update(MessageUpdateRequest request);
    void delete(UUID messageId);
}
