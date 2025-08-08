// ReadStatusService 인터페이스
package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusService {
    UUID create(ReadStatusCreateRequest request);
    Optional<ReadStatusResponse> find(UUID id);
    List<ReadStatusResponse> findAllByUserId(UUID userId);
    List<ReadStatusResponse> findAllByChannelId(UUID channelId);
    boolean update(ReadStatusUpdateRequest request);
    boolean delete(UUID id);

}
