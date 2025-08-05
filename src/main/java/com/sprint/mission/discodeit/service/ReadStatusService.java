package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

    ReadStatus create(ReadStatus readStatus);

    ReadStatus find(UUID id);

    List<ReadStatus> findAllByUserId(UUID userId);

    void delete(UUID id);
}
