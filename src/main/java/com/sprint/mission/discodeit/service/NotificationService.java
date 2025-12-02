package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.NotificationDto.Detail;
import java.util.List;
import java.util.UUID;

public interface NotificationService {

  List<Detail> findAllByUserId(UUID userId);

  void delete(UUID id);
}
