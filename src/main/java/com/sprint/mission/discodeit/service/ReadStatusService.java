package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.AddReadStatusDto;
import com.sprint.mission.discodeit.dto.request.UpdateMessageDto;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
    ReadStatus getReadStatus(UUID readStatusId);
    ReadStatus addReadStatus(AddReadStatusDto addReadStatusDto);
    void deleteReadStatus(UUID readStatusId);
    List<ReadStatus> getAllReadStatusByUserId(UUID userId);
    void deleteAllReadStatus();
    ReadStatus updateReadStatus(UUID readStatusId);
}
