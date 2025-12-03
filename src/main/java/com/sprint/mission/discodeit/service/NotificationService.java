package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.notification.NotificationCreateRequest;
import com.sprint.mission.discodeit.dto.notification.NotificationDto;

public interface NotificationService {

	NotificationDto create(NotificationCreateRequest request);

	List<NotificationDto> findByUserId(UUID userId);

	void delete(UUID id);
}
