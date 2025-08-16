package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusRepository extends Repository<ReadStatus, UUID> {
    List<ReadStatus> findAllByUserId(UUID userId);
}