package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.entity.ReadStatus;

public interface ReadStatusService {
    ReadStatus createStatus(ReadStatusDto.CreateReadStatus request);
}
