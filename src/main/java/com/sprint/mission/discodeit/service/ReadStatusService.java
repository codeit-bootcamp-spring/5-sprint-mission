package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readStatus.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.readStatus.ReadStatusUpdateDto;
import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    void create(ReadStatusCreateDto dto);
    ReadStatus findById(UUID id);
    List<ReadStatus> findAllByUserId(UUID userId);
    void update(ReadStatusUpdateDto dto);
    void deleteById(UUID id);
}
