package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;
import java.util.List;
import java.util.UUID;

public interface ReadStatusService {

  ReadStatus create(ReadStatusCreateRequest request);

  ReadStatus update(UUID readStatusId, ReadStatusUpdateRequest request);

  List<ReadStatus> findAllByUserId(UUID userId);

  void deleteByChannelId(UUID channelId);

  ReadStatus findById(UUID id);
}
