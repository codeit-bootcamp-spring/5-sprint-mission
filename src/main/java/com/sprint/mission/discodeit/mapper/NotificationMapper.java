package com.sprint.mission.discodeit.mapper;

import org.mapstruct.Mapper;

import com.sprint.mission.discodeit.dto.notification.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
	NotificationDto toDto(Notification notification);
}
