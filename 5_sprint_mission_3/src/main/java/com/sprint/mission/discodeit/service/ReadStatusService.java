package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatus create(ReadStatus readStatus);
    Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);
    List<ReadStatus> findAllByUserId(UUID userId);
    List<ReadStatus> findAllByChannelId(UUID channelId);
    ReadStatus update(ReadStatus readStatus);
    void delete(UUID messageId);

    ReadStatus create(ReadStatusCreateRequest readStatusCreateRequest);
    ReadStatus update(UUID id, ReadStatusUpdateRequest readStatusUpdateRequest);
}
