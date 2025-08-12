package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.*;


public interface ReadStatusService {
    ReadStatus create(ReadStatusCreateRequest request);
    Optional<ReadStatus> find(UUID userStatusId);
    List<ReadStatus> findAllByUserId(UUID userId);
    ReadStatus update(ReadStatusUpdateRequest request);
    void delete(UUID userStatusId);
}
