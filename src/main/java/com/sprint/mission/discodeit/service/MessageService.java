package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;

import java.util.*;

public interface MessageService {
    UUID create(MessageCreateRequest req);
    java.util.Optional<com.sprint.mission.discodeit.dto.response.MessageResponse> find(java.util.UUID id);
    java.util.List<com.sprint.mission.discodeit.dto.response.MessageResponse> findAllByChannelId(java.util.UUID channelId);
    boolean update(MessageUpdateRequest req);
    boolean delete(java.util.UUID id);
}