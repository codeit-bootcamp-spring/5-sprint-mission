package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.ReadStatusDto;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface ReadStatusService {
  // user의 모든 read-status. 없으면 빈 리스트 반환
  List<ReadStatusDto> findByUser(UUID userId);
  //(user, channel) 읽음 시각 갱신
  ReadStatusDto markRead(UUID userId, UUID channelId, ReadStatusUpdateRequest req);
}